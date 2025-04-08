package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Brand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class for Brand
 */
public class BrandDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetBrandDTO {
        private Long id;
        private String name;
        private String image;
        private Integer medicineCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetBrandDTOWithoutDetails {
        private Long id;
        private String name;
        private Integer medicineCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveBrandDTO {
        private Long id; // Optional for update

        @NotBlank(message = "Tên thương hiệu không được trống")
        private String name;

        private String image;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    public static GetBrandDTO fromEntity(Brand brand) {
        if (brand == null) return null;

        return GetBrandDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .image(brand.getImage())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }

    public static GetBrandDTOWithoutDetails fromEntityWithoutDetails(Brand brand) {
        if (brand == null) return null;

        return GetBrandDTOWithoutDetails.builder()
                .id(brand.getId())
                .name(brand.getName())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}