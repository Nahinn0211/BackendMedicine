package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Brand;
import hunre.edu.vn.backend.entity.Medicine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineDTO {
    private String code;
    private String name;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetMedicineDTO {
        private Long id;
        private String code;
        private String name;
        private String description;
        private String usageInstruction;
        private String dosageInstruction;
        private Boolean isPrescriptionRequired;
        private Integer totalStock;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private String origin;
        private BrandBasicDTO brand;
        private List<AttributeDTO.GetAttributeDTO> attributes;
        private List<CategoryDTO.GetCategoryDTO> categories;
        private List<MedicineMediaDTO.GetMedicineMediaDTO> medias;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveMedicineDTO {
        private Long id;

        @NotBlank(message = "Mã thuốc không được trống")
        private String code;

        @NotBlank(message = "Tên thuốc không được trống")
        private String name;

        private String description;
        private String usageInstruction;
        private String dosageInstruction;
        private Boolean isPrescriptionRequired;
        private Integer totalStock;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private String origin;

        @NotNull(message = "ID thương hiệu không được trống")
        private Long brandId;

        private List<Long> attributeIds;
        private List<Long> categoryIds;
        private List<Long> mediaIds;
    }

    public static GetMedicineDTO fromEntity(Medicine medicine) {
        if (medicine == null) return null;

        GetMedicineDTO dto = GetMedicineDTO.builder()
                .id(medicine.getId())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .code(medicine.getCode())
                .name(medicine.getName())
                .description(medicine.getDescription())
                .usageInstruction(medicine.getUsageInstruction())
                .dosageInstruction(medicine.getDosageInstruction())
                .isPrescriptionRequired(medicine.getIsPrescriptionRequired())
                .totalStock(medicine.getTotalStock())
                .minPrice(medicine.getMinPrice())
                .maxPrice(medicine.getMaxPrice())
                .origin(medicine.getOrigin())
                .brand(getBrandSafely(medicine.getBrand()))
                .build();

        dto.setAttributes(mapAttributesSafely(medicine));
        dto.setCategories(mapCategoriesSafely(medicine));
        dto.setMedias(mapMediaSafely(medicine));

        return dto;
    }

    private static BrandBasicDTO getBrandSafely(Brand brand) {
        if (brand == null) return null;
        return BrandBasicDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .image(brand.getImage())
                .build();
    }

    private static List<AttributeDTO.GetAttributeDTO> mapAttributesSafely(Medicine medicine) {
        return Optional.ofNullable(medicine.getAttributes())
                .map(attrs -> attrs.stream()
                        .filter(attr -> attr != null && Boolean.FALSE.equals(attr.getIsDeleted()))
                        .map(AttributeDTO::fromEntity)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    private static List<CategoryDTO.GetCategoryDTO> mapCategoriesSafely(Medicine medicine) {
        return Optional.ofNullable(medicine.getMedicineCategories())
                .map(categories -> categories.stream()
                        .filter(mc -> mc != null && Boolean.FALSE.equals(mc.getIsDeleted()))
                        .map(mc -> CategoryDTO.fromEntity(mc.getCategory()))
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    private static List<MedicineMediaDTO.GetMedicineMediaDTO> mapMediaSafely(Medicine medicine) {
        return Optional.ofNullable(medicine.getMedicineMedias())
                .map(medias -> medias.stream()
                        .filter(media -> media != null && Boolean.FALSE.equals(media.getIsDeleted()))
                        .map(MedicineMediaDTO::fromEntity)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandBasicDTO {
        private Long id;
        private String name;
        private String image;

        public static BrandBasicDTO getBrand(Brand brand) {
            if (brand == null) return null;

            return BrandBasicDTO.builder()
                    .id(brand.getId())
                    .name(brand.getName())
                    .image(brand.getImage())
                    .build();
        }
    }
}