package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Discount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountDTO {
    private String code;
    private String name;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetDiscountDTO {
        private Long id;
        private String code;
        private String name;
        private Long medicineId;
        private Double discountPercentage;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String description;
        private BigDecimal maxDiscountAmount;
        private BigDecimal minPurchaseAmount;
        private Integer usageLimit;
        private Integer currentUsageCount;
        private Boolean isValid;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveDiscountDTO {
        private Long id; // Optional for update

        @NotBlank(message = "Mã giảm giá không được trống")
        private String code;

        @NotBlank(message = "Tên giảm giá không được trống")
        private String name;

        @NotNull(message = "Phần trăm giảm giá không được trống")
        private Double discountPercentage;

        private Long medicineId;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String description;
        private BigDecimal maxDiscountAmount;
        private BigDecimal minPurchaseAmount;
        private Integer usageLimit;
    }

    // Static method to convert Entity to DTO
    public static GetDiscountDTO fromEntity(Discount discount) {
        if (discount == null) {
            return null;
        }

        return GetDiscountDTO.builder()
                .id(discount.getId())
                .code(discount.getCode())
                .name(discount.getName())
                .discountPercentage(discount.getDiscountPercentage())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .description(discount.getDescription())
                .maxDiscountAmount(discount.getMaxDiscountAmount())
                .minPurchaseAmount(discount.getMinPurchaseAmount())
                .usageLimit(discount.getUsageLimit())
                .currentUsageCount(discount.getCurrentUsageCount())
                .isValid(discount.isValid())
                .createdAt(discount.getCreatedAt())
                .updatedAt(discount.getUpdatedAt())
                .build();
    }
}