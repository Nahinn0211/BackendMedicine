package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.CategoryDTO;
import hunre.edu.vn.backend.dto.MedicineCategoryDTO;
import hunre.edu.vn.backend.entity.Category;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.MedicineCategory;
import hunre.edu.vn.backend.mapper.CategoryMapper;
import hunre.edu.vn.backend.mapper.MedicineCategoryMapper;
import hunre.edu.vn.backend.repository.CategoryRepository;
import hunre.edu.vn.backend.repository.MedicineCategoryRepository;
import hunre.edu.vn.backend.repository.MedicineRepository;
import hunre.edu.vn.backend.service.MedicineCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicineCategoryServiceImpl implements MedicineCategoryService {

    private final MedicineCategoryRepository medicineCategoryRepository;
    private final MedicineRepository medicineRepository;
    private final CategoryRepository categoryRepository;
    private final MedicineCategoryMapper medicineCategoryMapper;
    private final CategoryMapper categoryMapper;

    @Autowired
    public MedicineCategoryServiceImpl(
            MedicineCategoryRepository medicineCategoryRepository,
            MedicineRepository medicineRepository,
            CategoryRepository categoryRepository,
            MedicineCategoryMapper medicineCategoryMapper, CategoryMapper categoryMapper) {
        this.medicineCategoryRepository = medicineCategoryRepository;
        this.medicineRepository = medicineRepository;
        this.categoryRepository = categoryRepository;
        this.medicineCategoryMapper = medicineCategoryMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<MedicineCategoryDTO.GetMedicineCategoryDTO> findAll() {
        return medicineCategoryRepository.findAllActive().stream()
                .map(medicineCategoryMapper::toGetMedicineCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MedicineCategoryDTO.GetMedicineCategoryDTO> findById(Long id) {
        return medicineCategoryRepository.findActiveById(id)
                .map(medicineCategoryMapper::toGetMedicineCategoryDTO);
    }
    @Override
    public List<MedicineCategoryDTO.GetMedicineCategoryDTO> findMedicineCategoriesByMedicineId(Long medicineId) {
        List<MedicineCategory> medicineCategories = medicineCategoryRepository.findByMedicineId(medicineId);
        return medicineCategories.stream()
                .map(medicineCategoryMapper::toGetMedicineCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MedicineCategoryDTO.GetMedicineCategoryDTO saveOrUpdate(MedicineCategoryDTO.SaveMedicineCategoryDTO medicineCategoryDTO) {
        MedicineCategory medicineCategory;

        if (medicineCategoryDTO.getId() == null || medicineCategoryDTO.getId() == 0) {
            medicineCategory = new MedicineCategory();
            medicineCategory.setCreatedAt(LocalDateTime.now());
            medicineCategory.setUpdatedAt(LocalDateTime.now());
        } else {
            Optional<MedicineCategory> existingMedicineCategory = medicineCategoryRepository.findById(medicineCategoryDTO.getId());
            if (existingMedicineCategory.isEmpty()) {
                throw new RuntimeException("Medicine-Category relationship not found with ID: " + medicineCategoryDTO.getId());
            }
            medicineCategory = existingMedicineCategory.get();
            medicineCategory.setUpdatedAt(LocalDateTime.now());
        }

        Medicine medicine = medicineRepository.findActiveById(medicineCategoryDTO.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + medicineCategoryDTO.getMedicineId()));
        medicineCategory.setMedicine(medicine);

        Category category = categoryRepository.findActiveById(medicineCategoryDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + medicineCategoryDTO.getCategoryId()));
        medicineCategory.setCategory(category);

        MedicineCategory savedMedicineCategory = medicineCategoryRepository.save(medicineCategory);
        return medicineCategoryMapper.toGetMedicineCategoryDTO(savedMedicineCategory);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (medicineCategoryRepository.existsById(id)) {
                medicineCategoryRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " + ids.size() + " liên kết giữa thuốc và danh mục";
    }

    @Override
    public List<CategoryDTO.GetCategoryDTO> findMedicineCategoryDtoByMedicineId(Long medicineId) {
        List<MedicineCategory> medicineCategories = medicineCategoryRepository.findByMedicineId(medicineId);

        List<Long> categoryIds = medicineCategories.stream()
                .map(mc -> mc.getCategory().getId())
                .collect(Collectors.toList());

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        return categories.stream()
                .map(categoryMapper::toGetCategoryDTO)
                .collect(Collectors.toList());
    }
}