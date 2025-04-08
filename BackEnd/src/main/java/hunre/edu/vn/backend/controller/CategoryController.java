package hunre.edu.vn.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hunre.edu.vn.backend.dto.CategoryDTO;
import hunre.edu.vn.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO.GetCategoryDTO>> getAllCategories() {
        List<CategoryDTO.GetCategoryDTO> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO.GetCategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryDTO.GetCategoryDTO> saveOrUpdateCategory(@RequestPart("category") String categoryJson,@RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CategoryDTO.SaveCategoryDTO categoryDto = objectMapper.readValue(categoryJson, CategoryDTO.SaveCategoryDTO.class);

            if (categoryDto.getId() != null) {
                Optional<CategoryDTO.GetCategoryDTO> existingCategoryOpt = categoryService.findById(categoryDto.getId());
                if (existingCategoryOpt.isPresent()) {
                    CategoryDTO.GetCategoryDTO existingCategory = existingCategoryOpt.get();
                    if (file != null && !file.isEmpty()) {
                        categoryService.deleteCategoryImage(existingCategory.getImage());
                        String newImageUrl = categoryService.uploadCategoryImage(file);
                        categoryDto.setImage(newImageUrl);
                    } else {
                        categoryDto.setImage(existingCategory.getImage());
                    }
                    categoryDto.setUpdatedAt(LocalDateTime.now());
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                if (file != null && !file.isEmpty()) {
                    String newImageUrl = categoryService.uploadCategoryImage(file);
                    categoryDto.setImage(newImageUrl);
                }
                categoryDto.setCreatedAt(LocalDateTime.now());
                categoryDto.setUpdatedAt(LocalDateTime.now());
            }

            CategoryDTO.GetCategoryDTO savedCategory = categoryService.saveOrUpdate(categoryDto);

            return ResponseEntity.ok(savedCategory);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@RequestBody List<Long> ids) {
        return categoryService.deleteByList(ids);
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<CategoryDTO.GetCategoryDTO>> findByName(@RequestParam String name) {
        List<CategoryDTO.GetCategoryDTO> categories = categoryService.findByName(name);
        return ResponseEntity.ok(categories);
    }
}