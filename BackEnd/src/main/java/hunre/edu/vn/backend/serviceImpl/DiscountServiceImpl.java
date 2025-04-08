package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.DiscountDTO;
import hunre.edu.vn.backend.entity.Discount;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.mapper.DiscountMapper;
import hunre.edu.vn.backend.repository.DiscountRepository;
import hunre.edu.vn.backend.repository.MedicineRepository;
import hunre.edu.vn.backend.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final MedicineRepository medicineRepository;
    private final DiscountMapper discountMapper;

    @Autowired
    public DiscountServiceImpl(DiscountRepository discountRepository,
                               MedicineRepository medicineRepository,
                               DiscountMapper discountMapper) {
        this.discountRepository = discountRepository;
        this.medicineRepository = medicineRepository;
        this.discountMapper = discountMapper;
    }

    @Override
    public List<DiscountDTO.GetDiscountDTO> findAll() {
        return discountRepository.findAllActive().stream()
                .map(discountMapper::toGetDiscountDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DiscountDTO.GetDiscountDTO> findById(Long id) {
        return discountRepository.findActiveById(id)
                .map(discountMapper::toGetDiscountDTO);
    }

    @Override
    @Transactional
    public DiscountDTO.GetDiscountDTO saveOrUpdate(DiscountDTO.SaveDiscountDTO discountDTO) {
        Discount discount;

        if (discountDTO.getId() == null || discountDTO.getId() == 0) {
            // INSERT case
            discount = new Discount();
            discount.setCreatedAt(LocalDateTime.now());
            discount.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Discount> existingDiscount = discountRepository.findActiveById(discountDTO.getId());
            if (existingDiscount.isEmpty()) {
                throw new RuntimeException("Discount not found with ID: " + discountDTO.getId());
            }
            discount = existingDiscount.get();
            discount.setUpdatedAt(LocalDateTime.now());
        }

        // Xử lý medicine relationship
        if (discountDTO.getMedicineId() != null) {
            Medicine medicine = medicineRepository.findActiveById(discountDTO.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + discountDTO.getMedicineId()));
            discount.setMedicine(medicine);
        }

        // Cập nhật các trường khác
        discount.setCode(discountDTO.getCode());
        discount.setName(discountDTO.getName());
        discount.setDiscountPercentage(discountDTO.getDiscountPercentage());
        discount.setStartDate(discountDTO.getStartDate());
        discount.setEndDate(discountDTO.getEndDate());

        Discount savedDiscount = discountRepository.save(discount);
        return discountMapper.toGetDiscountDTO(savedDiscount);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (discountRepository.existsById(id)) {
                discountRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " + ids.size() + " discount";
    }

    @Override
    public Optional<DiscountDTO.GetDiscountDTO> findByCode(String code) {
        return discountRepository.findByCode(code)
                .map(discountMapper::toGetDiscountDTO);
    }

    @Override
    public List<DiscountDTO.GetDiscountDTO> findByMedicineId(Long medicineId) {
        return discountRepository.findByMedicine_Id(medicineId).stream()
                .map(discountMapper::toGetDiscountDTO)
                .collect(Collectors.toList());
    }
}