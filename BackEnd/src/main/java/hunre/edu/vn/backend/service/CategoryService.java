package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.CategoryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryDTO.GetCategoryDTO> findAll();
    Optional<CategoryDTO.GetCategoryDTO> findById(Long id);
    CategoryDTO.GetCategoryDTO saveOrUpdate(CategoryDTO.SaveCategoryDTO categoryDTO);
    String deleteByList(List<Long> ids);
    List<CategoryDTO.GetCategoryDTO> findByName(String name);
    String uploadCategoryImage(MultipartFile file) throws IOException;
    String deleteCategoryImage(String image);
}