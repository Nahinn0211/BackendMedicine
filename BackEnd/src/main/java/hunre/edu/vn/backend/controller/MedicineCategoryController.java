package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.CategoryDTO;
import hunre.edu.vn.backend.dto.MedicineCategoryDTO;
import hunre.edu.vn.backend.service.MedicineCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicine-categories")
public class MedicineCategoryController {

    private final MedicineCategoryService medicineCategoryService;

    public MedicineCategoryController(MedicineCategoryService medicineCategoryService) {
        this.medicineCategoryService = medicineCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<MedicineCategoryDTO.GetMedicineCategoryDTO>> getAllMedicineCategories() {
        List<MedicineCategoryDTO.GetMedicineCategoryDTO> categories = medicineCategoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineCategoryDTO.GetMedicineCategoryDTO> getMedicineCategoryById(@PathVariable Long id) {
        return medicineCategoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<List<CategoryDTO.GetCategoryDTO>> getMedicineCategoryByMedicineId(@PathVariable Long medicineId) {
        List<CategoryDTO.GetCategoryDTO> categories = medicineCategoryService.findMedicineCategoryDtoByMedicineId(medicineId);
        return ResponseEntity.ok(categories);
    }
    @GetMapping("/by-medicine/{medicineId}")
    public ResponseEntity<List<MedicineCategoryDTO.GetMedicineCategoryDTO>> getMedicineCategoriesByMedicineId(@PathVariable Long medicineId) {
        List<MedicineCategoryDTO.GetMedicineCategoryDTO> medicineCategories = medicineCategoryService.findMedicineCategoriesByMedicineId(medicineId);
        return ResponseEntity.ok(medicineCategories);
    }

    @PostMapping("/save")
    public ResponseEntity<MedicineCategoryDTO.GetMedicineCategoryDTO> saveOrUpdateMedicineCategory(@RequestBody MedicineCategoryDTO.SaveMedicineCategoryDTO medicineCategoryDTO) {
        MedicineCategoryDTO.GetMedicineCategoryDTO savedCategory = medicineCategoryService.saveOrUpdate(medicineCategoryDTO);
        return ResponseEntity.ok(savedCategory);
    }

    @DeleteMapping("/{id}")
    public String deleteMedicineCategory(@RequestBody List<Long> ids) {
        return medicineCategoryService.deleteByList(ids);
    }
}