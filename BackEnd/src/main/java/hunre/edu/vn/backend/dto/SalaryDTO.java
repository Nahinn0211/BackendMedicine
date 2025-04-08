package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.PaymentStatus;
import hunre.edu.vn.backend.entity.Salary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryDTO {
    private String code;
    private String name;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetSalaryDTO {
        private Long id;
        private Long userId;
        private UserDTO.GetUserDTO user;
        private String bankCode;
        private String bankName;
        private BigDecimal price;
        private PaymentStatus status;
        private LocalDate paymentDate;
        private String note;
        private Boolean isPayable;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveSalaryDTO {
        private Long id; // Optional for update
        @NotNull(message = "ID người dùng không được trống")
        private Long userId;
        @NotBlank(message = "Số tài khoản ngân hàng không được trống")
        private String bankCode;
        @NotBlank(message = "Tên ngân hàng không được trống")
        private String bankName;
        @NotBlank(message = "Số tiền không được trống")
        private BigDecimal price;
        private PaymentStatus status;
        private LocalDate paymentDate;
        private String note;
        private Boolean isPayable;
    }

    // Static method to convert Entity to DTO
    public static GetSalaryDTO fromEntity(Salary salary) {
        if (salary == null) return null;

        return GetSalaryDTO.builder()
                .id(salary.getId())
                .user(UserDTO.fromEntity(salary.getUser()))
                .bankCode(salary.getBankCode())
                .bankName(salary.getBankName())
                .price(salary.getPrice())
                .status(salary.getStatus() != null ? PaymentStatus.valueOf(salary.getStatus().name()) : null)
                .paymentDate(salary.getPaymentDate())
                .note(salary.getNote())
                .isPayable(salary.isPayable())
                .createdAt(salary.getCreatedAt())
                .updatedAt(salary.getUpdatedAt())
                .build();
    }
}