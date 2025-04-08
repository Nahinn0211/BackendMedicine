package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.MedicineCategory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineCategoryDTO {
    private CategoryDTO category;
    private MedicineDTO medicine;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetMedicineCategoryDTO {
        private Long id;
        private Long medicineId;
        private Long categoryId;
        private CategoryDTO.GetCategoryDTO category;
        private MedicineDTO.GetMedicineDTO medicine;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveMedicineCategoryDTO {
        private Long id; // Optional for update

        @NotNull(message = "ID danh mục không được trống")
        private Long categoryId;

        @NotNull(message = "ID thuốc không được trống")
        private Long medicineId;
    }

    // Static method to convert Entity to DTO
    public static GetMedicineCategoryDTO fromEntity(MedicineCategory medicineCategory) {
        if (medicineCategory == null) {
            return null;
        }

        return GetMedicineCategoryDTO.builder()
                .id(medicineCategory.getId())
                .category(CategoryDTO.fromEntity(medicineCategory.getCategory()))
                .medicine(MedicineDTO.fromEntity(medicineCategory.getMedicine()))
                .createdAt(medicineCategory.getCreatedAt())
                .updatedAt(medicineCategory.getUpdatedAt())
                .build();
    }
}