package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Voucher;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO Class for Voucher
 */
public class VoucherDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetVoucherDTO {
        private Long id;
        private String code;
        private Double voucherPercentage;
        private Integer stock;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private BigDecimal minimumOrderValue;
        private Voucher.VoucherStatus status;
        private Boolean isValid;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetVoucherDTOWithoutDetails {
        private Long id;
        private String code;
        private Double voucherPercentage;
        private Voucher.VoucherStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveVoucherDTO {
        private Long id; // Optional for update

        @NotBlank(message = "Mã voucher không được để trống")
        private String code;

        @NotNull(message = "Phần trăm giảm giá không được để trống")
        @Positive(message = "Phần trăm giảm giá phải là số dương")
        private Double voucherPercentage;

        @NotNull(message = "Số lượng voucher không được để trống")
        @Positive(message = "Số lượng voucher phải là số dương")
        private Integer stock;

        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private BigDecimal minimumOrderValue;
    }

    public static GetVoucherDTO fromEntity(Voucher voucher) {
        if (voucher == null) {
            return null;
        }

        return GetVoucherDTO.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .voucherPercentage(voucher.getVoucherPercentage())
                .stock(voucher.getStock())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .minimumOrderValue(voucher.getMinimumOrderValue())
                .status(voucher.getStatus())
                .isValid(voucher.isValid())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .build();
    }

    public static GetVoucherDTOWithoutDetails fromEntityWithoutDetails(Voucher voucher) {
        if (voucher == null) {
            return null;
        }

        return GetVoucherDTOWithoutDetails.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .voucherPercentage(voucher.getVoucherPercentage())
                .status(voucher.getStatus())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .build();
    }
}