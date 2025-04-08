package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.CategoryDTO;
import hunre.edu.vn.backend.entity.Category;
import hunre.edu.vn.backend.mapper.CategoryMapper;
import hunre.edu.vn.backend.repository.CategoryRepository;
import hunre.edu.vn.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final S3Service s3Service;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, S3Service s3Service) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.s3Service = s3Service;
    }

    @Override
    public List<CategoryDTO.GetCategoryDTO> findAll() {
        return categoryRepository.findAllActive().stream()
                .map(categoryMapper::toGetCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoryDTO.GetCategoryDTO> findById(Long id) {
        return categoryRepository.findActiveById(id)
                .map(categoryMapper::toGetCategoryDTO);
    }

    @Override
    public CategoryDTO.GetCategoryDTO saveOrUpdate(CategoryDTO.SaveCategoryDTO categoryDTO) {
        Category category;

        if (categoryDTO.getId() == null || categoryDTO.getId() == 0) {
            category = categoryMapper.toEntity(categoryDTO);
            category.setCreatedAt(LocalDateTime.now());
            category.setUpdatedAt(LocalDateTime.now());

            if (categoryDTO.getParentId() != null && categoryDTO.getParentId() > 0) {
                Category parent = categoryRepository.findById(categoryDTO.getParentId())
                        .orElseThrow(() -> new RuntimeException("Parent category not found with ID: " + categoryDTO.getParentId()));
                category.setParent(parent);
            }
        } else {
            Optional<Category> existingCategory = categoryRepository.findActiveById(categoryDTO.getId());
            if (existingCategory.isEmpty()) {
                throw new RuntimeException("Category not found with ID: " + categoryDTO.getId());
            }

            category = existingCategory.get();
            category.setName(categoryDTO.getName());
            category.setImage(categoryDTO.getImage());

            if (categoryDTO.getParentId() != null) {
                if (categoryDTO.getParentId() > 0) {
                    Category parent = categoryRepository.findActiveById(categoryDTO.getParentId())
                            .orElseThrow(() -> new RuntimeException("Parent category not found with ID: " + categoryDTO.getParentId()));
                    category.setParent(parent);
                } else {
                    category.setParent(null);
                }
            }

            category.setUpdatedAt(LocalDateTime.now());
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toGetCategoryDTO(savedCategory);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (categoryRepository.existsById(id)) {
                categoryRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " + ids.size() + " danh mục.";
    }

    @Override
    public List<CategoryDTO.GetCategoryDTO> findByName(String name) {
        return categoryRepository.findByNameAndIsDeletedFalse(name).stream()
                .map(categoryMapper::toGetCategoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteCategoryImage(String image) {
        if (image != null && !image.isEmpty()) {
            try {
                s3Service.deleteFile(image);
                return "Đã xóa ảnh danh mục";
            } catch (Exception e) {
                return "Có lỗi khi xóa ảnh: " + e.getMessage();
            }
        }
        return "Không thể xóa ảnh";
    }

    @Override
    public String uploadCategoryImage(MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }
}