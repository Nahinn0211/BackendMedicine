package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Attribute;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO Class for Attribute
 */
public class AttributeDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAttributeDTO {
        private Long id;
        private Long medicineId;
        private String name;
        private BigDecimal priceIn;
        private BigDecimal priceOut;
        private Integer stock;
        private LocalDate expiryDate;
        private Boolean isExpired;
        private Boolean isNearExpiry;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAttributeDTOWithoutDetails {
        private Long id;
        private Long medicineId;
        private String medicineName;
        private String name;
        private BigDecimal priceOut;
        private Integer stock;
        private LocalDate expiryDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveAttributeDTO {
        private Long id;

        @NotNull(message = "Thuốc không được trống")
        private Long medicineId;

        @NotBlank(message = "Tên thuộc tính không được trống")
        private String name;

        @NotNull(message = "Giá nhập không được trống")
        private BigDecimal priceIn;

        @NotNull(message = "Giá bán không được trống")
        private BigDecimal priceOut;

        @NotNull(message = "Số lượng không được trống")
        private Integer stock;

        @NotNull(message = "Ngày hết hạn không được trống")
        private LocalDate expiryDate;

        private Boolean isExpired;
        private Boolean isNearExpiry;
    }

    public static GetAttributeDTO fromEntity(Attribute attribute) {
        if (attribute == null) return null;

        return GetAttributeDTO.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .priceIn(attribute.getPriceIn())
                .priceOut(attribute.getPriceOut())
                .stock(attribute.getStock())
                .expiryDate(attribute.getExpiryDate())
                .isExpired(attribute.isExpired())
                .isNearExpiry(attribute.isNearExpiry())
                .createdAt(attribute.getCreatedAt())
                .updatedAt(attribute.getUpdatedAt())
                .build();
    }

    public static GetAttributeDTOWithoutDetails fromEntityWithoutDetails(Attribute attribute) {
        if (attribute == null) return null;

        return GetAttributeDTOWithoutDetails.builder()
                .id(attribute.getId())
                .medicineId(attribute.getMedicine() != null ?
                        attribute.getMedicine().getId() : null)
                .medicineName(attribute.getMedicine() != null ?
                        attribute.getMedicine().getName() : null)
                .name(attribute.getName())
                .priceOut(attribute.getPriceOut())
                .stock(attribute.getStock())
                .expiryDate(attribute.getExpiryDate())
                .createdAt(attribute.getCreatedAt())
                .updatedAt(attribute.getUpdatedAt())
                .build();
    }
}