package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.MedicineDTO;
import hunre.edu.vn.backend.entity.Brand;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.mapper.MedicineMapper;
import hunre.edu.vn.backend.repository.BrandRepository;
import hunre.edu.vn.backend.repository.MedicineRepository;
import hunre.edu.vn.backend.repository.OrderDetailRepository;
import hunre.edu.vn.backend.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.ion.Decimal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final BrandRepository brandRepository;
    private final MedicineMapper medicineMapper;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public MedicineServiceImpl(
            MedicineRepository medicineRepository,
            BrandRepository brandRepository,
            MedicineMapper medicineMapper, OrderDetailRepository orderDetailRepository) {
        this.medicineRepository = medicineRepository;
        this.brandRepository = brandRepository;
        this.medicineMapper = medicineMapper;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public List<MedicineDTO.GetMedicineDTO> findAll() {
        return medicineRepository.findAllActive().stream()
                .map(medicineMapper::toGetMedicineDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MedicineDTO.GetMedicineDTO> findById(Long id) {
        return medicineRepository.findActiveById(id)
                .map(medicineMapper::toGetMedicineDTO);
    }

    @Override
    @Transactional
    public MedicineDTO.GetMedicineDTO saveOrUpdate(MedicineDTO.SaveMedicineDTO medicineDTO) {
        Medicine medicine;

        if (medicineDTO.getId() == null || medicineDTO.getId() == 0) {
            // INSERT case
            medicine = new Medicine();
            medicine.setCreatedAt(LocalDateTime.now());
            medicine.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Medicine> existingMedicine = medicineRepository.findActiveById(medicineDTO.getId());
            if (existingMedicine.isEmpty()) {
                throw new RuntimeException("Medicine not found with ID: " + medicineDTO.getId());
            }
            medicine = existingMedicine.get();
            medicine.setUpdatedAt(LocalDateTime.now());
        }

        if (medicineDTO.getBrandId() != null) {
            Brand brand = brandRepository.findActiveById(medicineDTO.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found with ID: " + medicineDTO.getBrandId()));
            System.out.println("Brand ID: " + brand.getId());  // 🔍 Kiểm tra xem brand có ID không
            System.out.println("Brand Name: " + brand.getName());
            medicine.setBrand(brand);
        }

        // Cập nhật các trường cơ bản
        medicine.setCode(medicineDTO.getCode());
        medicine.setName(medicineDTO.getName());
        medicine.setDescription(medicineDTO.getDescription());
        medicine.setOrigin(medicineDTO.getOrigin());

        Medicine savedMedicine = medicineRepository.save(medicine);
        System.out.println(savedMedicine.toString());
        return medicineMapper.toGetMedicineDTO(savedMedicine);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (medicineRepository.existsById(id)){
                medicineRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " + ids.size() + " thuốc";
    }

    @Override
    public Optional<MedicineDTO.GetMedicineDTO> findByCode(String code) {
        return medicineRepository.findByCode(code)
                .map(medicineMapper::toGetMedicineDTO);
    }

    @Override
    public List<MedicineDTO.GetMedicineDTO> findByName(String name) {
        return medicineRepository.findByName(name).stream()
                .map(medicineMapper::toGetMedicineDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineDTO.GetMedicineDTO> seach(String name, Long categoryId, Long brandId, Decimal rangePrice, String sortBy) {
        List<Medicine> medicines = medicineRepository.findAll().stream()
                .filter(medicine -> name == null || medicine.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(medicine -> categoryId == null ||
                        medicine.getMedicineCategories().stream()
                                .anyMatch(mc -> mc.getCategory().getId().equals(categoryId)))
                .filter(medicine -> brandId == null || (medicine.getBrand() != null && medicine.getBrand().getId().equals(brandId)))
                // Use the first attribute's price_out for filtering
                .filter(medicine -> rangePrice == null ||
                        (medicine.getAttributes() != null && !medicine.getAttributes().isEmpty() &&
                                medicine.getAttributes().get(0).getPriceOut().compareTo(rangePrice) <= 0))
                .collect(Collectors.toList());

        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "price_asc":
                    medicines.sort((m1, m2) -> {
                        BigDecimal price1 = m1.getAttributes() != null && !m1.getAttributes().isEmpty()
                                ? m1.getAttributes().get(0).getPriceOut()
                                : BigDecimal.ZERO;
                        BigDecimal price2 = m2.getAttributes() != null && !m2.getAttributes().isEmpty()
                                ? m2.getAttributes().get(0).getPriceOut()
                                : BigDecimal.ZERO;
                        return price1.compareTo(price2);
                    });
                    break;
                case "price_desc":
                    medicines.sort((m1, m2) -> {
                        BigDecimal price1 = m1.getAttributes() != null && !m1.getAttributes().isEmpty()
                                ? m1.getAttributes().get(0).getPriceOut()
                                : BigDecimal.ZERO;
                        BigDecimal price2 = m2.getAttributes() != null && !m2.getAttributes().isEmpty()
                                ? m2.getAttributes().get(0).getPriceOut()
                                : BigDecimal.ZERO;
                        return price2.compareTo(price1);
                    });
                    break;
                case "name_asc":
                    medicines.sort(Comparator.comparing(Medicine::getName));
                    break;
                case "name_desc":
                    medicines.sort(Comparator.comparing(Medicine::getName).reversed());
                    break;
                default:
            }
        }

        return medicines.stream()
                .map(medicineMapper::toGetMedicineDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineDTO.GetMedicineDTO> getBestSaling() {
        return medicineRepository.findAll().stream()
                .sorted((m1, m2) -> {
                    long salesCountM1 = orderDetailRepository.sumQuantityByMedicineId(m1.getId());
                    long salesCountM2 = orderDetailRepository.sumQuantityByMedicineId(m2.getId());
                    return Long.compare(salesCountM2, salesCountM1);
                })
                .limit(10)
                .map(medicineMapper::toGetMedicineDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineDTO.GetMedicineDTO> getMedicineNew() {
        return medicineRepository.findAll().stream()
                .sorted(Comparator.comparing(Medicine::getCreatedAt).reversed())
                .limit(10) // Top 10 newest medicines
                .map(medicineMapper::toGetMedicineDTO)
                .collect(Collectors.toList());
    }
}