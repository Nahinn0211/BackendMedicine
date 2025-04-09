package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.AttributeDTO;
import hunre.edu.vn.backend.dto.CategoryDTO;
import hunre.edu.vn.backend.dto.MedicineCategoryDTO;
import hunre.edu.vn.backend.dto.MedicineDTO;
import hunre.edu.vn.backend.dto.MedicineMediaDTO;
import hunre.edu.vn.backend.entity.Brand;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.mapper.MedicineMapper;
import hunre.edu.vn.backend.repository.BrandRepository;
import hunre.edu.vn.backend.repository.MedicineRepository;
import hunre.edu.vn.backend.repository.OrderDetailRepository;
import hunre.edu.vn.backend.service.AttributeService;
import hunre.edu.vn.backend.service.MedicineCategoryService;
import hunre.edu.vn.backend.service.MedicineMediaService;
import hunre.edu.vn.backend.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.ion.Decimal;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final BrandRepository brandRepository;
    private final MedicineMapper medicineMapper;
    private final OrderDetailRepository orderDetailRepository;
    private final MedicineMediaService medicineMediaService;
    private final MedicineCategoryService medicineCategoryService;
    private final AttributeService attributeService;

    @Autowired
    public MedicineServiceImpl(
            MedicineRepository medicineRepository,
            BrandRepository brandRepository,
            MedicineMapper medicineMapper,
            OrderDetailRepository orderDetailRepository,
            MedicineMediaService medicineMediaService,
            MedicineCategoryService medicineCategoryService,
            AttributeService attributeService) {
        this.medicineRepository = medicineRepository;
        this.brandRepository = brandRepository;
        this.medicineMapper = medicineMapper;
        this.orderDetailRepository = orderDetailRepository;
        this.medicineMediaService = medicineMediaService;
        this.medicineCategoryService = medicineCategoryService;
        this.attributeService = attributeService;
    }

    @Override
    @Transactional
    public Map<String, Object> saveOrUpdateMedicineWithDetails(
            MedicineDTO.SaveMedicineDTO medicineDTO,
            MultipartFile[] files,
            List<Long> categoryIds,
            Integer mainImageIndex) throws IOException {

        // Create response object
        Map<String, Object> response = new HashMap<>();
        List<MedicineMediaDTO.GetMedicineMediaDTO> savedMedias = new ArrayList<>();
        List<CategoryDTO.GetCategoryDTO> savedCategories = new ArrayList<>();
        List<AttributeDTO.GetAttributeDTO> savedAttributes = new ArrayList<>();

        try {
            // Step 1: Save base medicine information
            MedicineDTO.GetMedicineDTO savedMedicine = this.saveOrUpdate(medicineDTO);
            Long medicineId = savedMedicine.getId();

            // Step 2: Process and save medicine attributes
            if (medicineDTO.getAttributes() != null && !medicineDTO.getAttributes().isEmpty()) {
                // Ensure each attribute has the correct medicineId
                medicineDTO.getAttributes().forEach(attr -> attr.setMedicineId(medicineId));
                savedAttributes = attributeService.saveOrUpdateAll(medicineDTO.getAttributes());
            }

            // Step 3: Process and save categories
            if (categoryIds != null && !categoryIds.isEmpty()) {
                // Get list of existing categories for this medicine
                List<CategoryDTO.GetCategoryDTO> existingCategories =
                        medicineCategoryService.findMedicineCategoryDtoByMedicineId(medicineId);

                // Create a set of existing category IDs
                Set<Long> existingCategoryIds = existingCategories.stream()
                        .map(CategoryDTO.GetCategoryDTO::getId)
                        .collect(Collectors.toSet());

                // Only link with categories that don't already exist
                categoryIds.stream()
                        .filter(catId -> !existingCategoryIds.contains(catId))
                        .forEach(categoryId -> {
                            MedicineCategoryDTO.SaveMedicineCategoryDTO categoryDTO =
                                    new MedicineCategoryDTO.SaveMedicineCategoryDTO();
                            categoryDTO.setMedicineId(medicineId);
                            categoryDTO.setCategoryId(categoryId);
                            medicineCategoryService.saveOrUpdate(categoryDTO);
                        });

                // Retrieve updated category list
                savedCategories = medicineCategoryService.findMedicineCategoryDtoByMedicineId(medicineId);
            }

            // Step 4: Process and save images
            if (files != null && files.length > 0) {
                // First, clear existing main image flag if we're setting a new main image
                if (mainImageIndex != null && mainImageIndex >= 0 && mainImageIndex < files.length) {
                    Optional<MedicineMediaDTO.GetMedicineMediaDTO> existingMainImage =
                            medicineMediaService.findMainImageByMedicineId(medicineId);

                    existingMainImage.ifPresent(mainImg -> {
                        MedicineMediaDTO.SaveMedicineMediaDTO updateDTO = new MedicineMediaDTO.SaveMedicineMediaDTO();
                        updateDTO.setId(mainImg.getId());
                        updateDTO.setMedicineId(medicineId);
                        updateDTO.setMediaUrl(mainImg.getMediaUrl());
                        updateDTO.setMainImage(false);
                        medicineMediaService.saveOrUpdate(updateDTO);
                    });
                }

                // Process and save new images
                for (int i = 0; i < files.length; i++) {
                    MultipartFile file = files[i];
                    if (!file.isEmpty()) {
                        // Upload image
                        String mediaUrl = medicineMediaService.uploadMedicineImage(file);

                        // Create MedicineMedia object
                        MedicineMediaDTO.SaveMedicineMediaDTO mediaDTO = new MedicineMediaDTO.SaveMedicineMediaDTO();
                        mediaDTO.setMedicineId(medicineId);
                        mediaDTO.setMediaUrl(mediaUrl);

                        // Set main image flag
                        boolean isMainImage = (mainImageIndex != null && i == mainImageIndex);
                        mediaDTO.setMainImage(isMainImage);

                        // Save to DB
                        MedicineMediaDTO.GetMedicineMediaDTO savedMedia =
                                medicineMediaService.saveOrUpdate(mediaDTO);
                        savedMedias.add(savedMedia);
                    }
                }
            }

            // Get the updated medicine after all related records are saved
            Optional<MedicineDTO.GetMedicineDTO> updatedMedicine = findById(medicineId);

            // Create response
            response.put("medicine", updatedMedicine.orElse(savedMedicine));
            response.put("medias", savedMedias);
            response.put("categories", savedCategories);
            response.put("attributes", savedAttributes);

            return response;

        } catch (Exception e) {
            // Log error and rethrow to trigger transaction rollback
            e.printStackTrace();
            throw new RuntimeException("Error saving medicine with details: " + e.getMessage(), e);
        }
    }

    @Override
    public List<MedicineDTO.GetMedicineDTO> findAll() {
        // Lấy danh sách thuốc từ repository
        List<Medicine> medicines = medicineRepository.findAllActive();

        // Chuyển đổi thành DTO và đảm bảo mỗi DTO có danh sách thuộc tính
        return medicines.stream()
                .map(medicine -> {
                    // Sử dụng mapper để chuyển đổi đối tượng medicine thành DTO
                    MedicineDTO.GetMedicineDTO dto = medicineMapper.toGetMedicineDTO(medicine);

                    // Lấy danh sách thuộc tính theo medicineId
                    List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(medicine.getId());

                    // Đặt danh sách thuộc tính vào DTO
                    dto.setAttributes(attributes);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MedicineDTO.GetMedicineDTO> findById(Long id) {
        return medicineRepository.findActiveById(id)
                .map(medicine -> {
                    // Chuyển đổi thành DTO
                    MedicineDTO.GetMedicineDTO dto = medicineMapper.toGetMedicineDTO(medicine);

                    // Lấy và đặt danh sách thuộc tính theo medicineId
                    List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(id);
                    dto.setAttributes(attributes);

                    // Đảm bảo danh mục và phương tiện cũng được lấy đúng cách
                    if (dto.getCategories() == null) {
                        dto.setCategories(medicineCategoryService.findMedicineCategoryDtoByMedicineId(id));
                    }

                    if (dto.getMedias() == null) {
                        dto.setMedias(medicineMediaService.findByMedicineId(id));
                    }

                    return dto;
                });
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
            medicine.setBrand(brand);
        }

        // Cập nhật các trường cơ bản
        medicine.setCode(medicineDTO.getCode());
        medicine.setName(medicineDTO.getName());
        medicine.setDescription(medicineDTO.getDescription());
        medicine.setOrigin(medicineDTO.getOrigin());
        medicine.setIsPrescriptionRequired(medicineDTO.getIsPrescriptionRequired());
        medicine.setUsageInstruction(medicineDTO.getUsageInstruction());
        medicine.setDosageInstruction(medicineDTO.getDosageInstruction());

        Medicine savedMedicine = medicineRepository.save(medicine);

        // Lưu attributes nếu có
        if (medicineDTO.getAttributes() != null && !medicineDTO.getAttributes().isEmpty()) {
            // Đảm bảo mỗi thuộc tính có medicineId chính xác
            for (AttributeDTO.SaveAttributeDTO attributeDTO : medicineDTO.getAttributes()) {
                attributeDTO.setMedicineId(savedMedicine.getId());
                attributeService.saveOrUpdate(attributeDTO);
            }
        }

        // Tạo DTO từ entity
        MedicineDTO.GetMedicineDTO dto = medicineMapper.toGetMedicineDTO(savedMedicine);

        // Thêm danh sách thuộc tính vào DTO trả về
        List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(savedMedicine.getId());
        dto.setAttributes(attributes);

        return dto;
    }

    @Override
    @Transactional
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (medicineRepository.existsById(id)){
                // Xóa các bản ghi liên quan
                attributeService.deleteAllByMedicineId(id);

                // Xóa thuốc
                medicineRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " + ids.size() + " thuốc";
    }

    @Override
    public Optional<MedicineDTO.GetMedicineDTO> findByCode(String code) {
        return medicineRepository.findByCode(code)
                .map(medicine -> {
                    // Chuyển đổi đối tượng medicine thành DTO
                    MedicineDTO.GetMedicineDTO dto = medicineMapper.toGetMedicineDTO(medicine);

                    // Lấy danh sách thuộc tính theo medicineId
                    List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(medicine.getId());
                    dto.setAttributes(attributes);

                    return dto;
                });
    }

    @Override
    public List<MedicineDTO.GetMedicineDTO> findByName(String name) {
        return medicineRepository.findByName(name).stream()
                .map(medicine -> {
                    // Chuyển đổi đối tượng medicine thành DTO
                    MedicineDTO.GetMedicineDTO dto = medicineMapper.toGetMedicineDTO(medicine);

                    // Lấy danh sách thuộc tính theo medicineId
                    List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(medicine.getId());
                    dto.setAttributes(attributes);

                    return dto;
                })
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
                .collect(Collectors.toList());

        // Lấy và kiểm tra dữ liệu thuộc tính
        List<MedicineDTO.GetMedicineDTO> dtos = new ArrayList<>();
        for (Medicine medicine : medicines) {
            List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(medicine.getId());

            // Kiểm tra giá từ thuộc tính (attribute)
            if (rangePrice != null) {
                if (attributes.isEmpty() || attributes.stream()
                        .noneMatch(attr -> attr.getPriceOut().compareTo(
                                BigDecimal.valueOf(rangePrice.doubleValue())) <= 0)) {
                    continue; // Bỏ qua thuốc này nếu không có thuộc tính phù hợp với giá
                }
            }

            // Tạo DTO và thêm thuộc tính
            MedicineDTO.GetMedicineDTO dto = medicineMapper.toGetMedicineDTO(medicine);
            dto.setAttributes(attributes);
            dtos.add(dto);
        }

        // Sắp xếp kết quả nếu cần
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "price_asc":
                    dtos.sort((m1, m2) -> {
                        BigDecimal price1 = getLowestPrice(m1.getAttributes());
                        BigDecimal price2 = getLowestPrice(m2.getAttributes());
                        return price1.compareTo(price2);
                    });
                    break;
                case "price_desc":
                    dtos.sort((m1, m2) -> {
                        BigDecimal price1 = getLowestPrice(m1.getAttributes());
                        BigDecimal price2 = getLowestPrice(m2.getAttributes());
                        return price2.compareTo(price1);
                    });
                    break;
                case "name_asc":
                    dtos.sort(Comparator.comparing(MedicineDTO.GetMedicineDTO::getName));
                    break;
                case "name_desc":
                    dtos.sort(Comparator.comparing(MedicineDTO.GetMedicineDTO::getName).reversed());
                    break;
                default:
            }
        }

        return dtos;
    }

    // Helper method để lấy giá thấp nhất từ các attributes của thuốc
    private BigDecimal getLowestPrice(List<AttributeDTO.GetAttributeDTO> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return attributes.stream()
                .map(AttributeDTO.GetAttributeDTO::getPriceOut)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
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
                .map(medicine -> {
                    // Tạo DTO
                    MedicineDTO.GetMedicineDTO dto = medicineMapper.toGetMedicineDTO(medicine);

                    // Lấy danh sách thuộc tính theo medicineId
                    List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(medicine.getId());
                    dto.setAttributes(attributes);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineDTO.GetMedicineDTO> getMedicineNew() {
        return medicineRepository.findAll().stream()
                .sorted(Comparator.comparing(Medicine::getCreatedAt).reversed())
                .limit(10) // Top 10 newest medicines
                .map(medicine -> {
                    // Tạo DTO
                    MedicineDTO.GetMedicineDTO dto = medicineMapper.toGetMedicineDTO(medicine);

                    // Lấy danh sách thuộc tính theo medicineId
                    List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(medicine.getId());
                    dto.setAttributes(attributes);

                    return dto;
                })
                .collect(Collectors.toList());
    }
}