package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.MedicineMedia;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineMediaDTO {
    private String mediaUrl;
    private Boolean mainImage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetMedicineMediaDTO {
        private Long id;
        private Long medicineId;
        private String mediaUrl;
        private Boolean mainImage;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveMedicineMediaDTO {
        private Long id; // Optional for update

        @NotBlank(message = "URL media không được trống")
        private String mediaUrl;
        private Boolean mainImage;
        private Long medicineId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    // Static method to convert Entity to DTO
    public static GetMedicineMediaDTO fromEntity(MedicineMedia medicineMedia) {
        if (medicineMedia == null) return null;

        return GetMedicineMediaDTO.builder()
                .id(medicineMedia.getId())
                .createdAt(medicineMedia.getCreatedAt())
                .updatedAt(medicineMedia.getUpdatedAt())
                .mediaUrl(medicineMedia.getMediaUrl())
                .mainImage(medicineMedia.getMainImage())
                .build();
    }
}