package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.CategoryDTO;
import hunre.edu.vn.backend.dto.MedicineCategoryDTO;

import java.util.List;
import java.util.Optional;

public interface MedicineCategoryService {
    List<MedicineCategoryDTO.GetMedicineCategoryDTO> findAll();
    Optional<MedicineCategoryDTO.GetMedicineCategoryDTO> findById(Long id);
    List<MedicineCategoryDTO.GetMedicineCategoryDTO> findMedicineCategoriesByMedicineId(Long medicineId);
    MedicineCategoryDTO.GetMedicineCategoryDTO saveOrUpdate(MedicineCategoryDTO.SaveMedicineCategoryDTO medicineCategoryDTO);
    String deleteByList(List<Long> ids);
    List<CategoryDTO.GetCategoryDTO> findMedicineCategoryDtoByMedicineId(Long medicineId);
}