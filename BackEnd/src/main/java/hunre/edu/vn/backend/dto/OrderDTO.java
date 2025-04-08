package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Order;
import hunre.edu.vn.backend.entity.OrderStatus;
import hunre.edu.vn.backend.entity.PaymentMethod;
import hunre.edu.vn.backend.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class for Order
 */
public class OrderDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetOrderDTO {
        private Long id;
        private Long patientId;
        private String orderCode;
        private PatientProfileDTO.GetPatientProfileDTO patient;
        private String patientName;
        private BigDecimal totalPrice;
        private PaymentMethod paymentMethod;
        private OrderStatus status;
        private PaymentStatus paymentStatus;
        private String voucherCode;
        private BigDecimal discountAmount;
        private String note;
        private LocalDateTime completedAt;
        private LocalDateTime cancelledAt;
        private String cancelledReason;
        private Integer itemCount;
        private List<OrderDetailDTO.GetOrderDetailDTO> orderDetails;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveOrderDTO {
        private Long id; // Optional for update

        @NotNull(message = "Mã đơn hàng không được trống")
        private String orderCode;

        @NotNull(message = "ID bệnh nhân không được trống")
        private Long patientId;

        private PaymentMethod paymentMethod;
        private OrderStatus status;
        private PaymentStatus paymentStatus;
        private String voucherCode;
        private BigDecimal discountAmount;
        private String note;
        private BigDecimal totalPrice;
    }

    // Static method to convert Entity to DTO
    public static GetOrderDTO fromEntity(Order order) {
        if (order == null) {
            return null;
        }

        return GetOrderDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .patient(PatientProfileDTO.fromEntity(order.getPatient()))
                .totalPrice(order.getTotalPrice())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .voucherCode(order.getVoucherCode())
                .discountAmount(order.getDiscountAmount())
                .note(order.getNote())
                .completedAt(order.getCompletedAt())
                .cancelledAt(order.getCancelledAt())
                .cancelledReason(order.getCancelledReason())
                .itemCount(order.getItemCount())
                .orderDetails(order.getOrderDetails() != null ?
                        order.getOrderDetails().stream()
                                .map(OrderDetailDTO::fromEntity)
                                .collect(Collectors.toList()) : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderFullDetailDto {
        // Thông tin đơn hàng
        private Long id;
        private String orderCode;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private BigDecimal totalPrice;
        private OrderStatus status;
        private PaymentMethod paymentMethod;
        private BigDecimal discountAmount;
        private String note;
        private String voucherCode;

        // Thông tin người dùng
        private String patientName;
        private String patientEmail;

        // Chi tiết đơn hàng
        private List<OrderDetailFullInfoDto> orderDetails;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OrderDetailFullInfoDto {
            // Thông tin chi tiết đơn hàng
            private Long id;
            private Integer quantity;
            private BigDecimal unitPrice;

            // Thông tin thuốc
            private MedicineDTO.GetMedicineDTO medicine;

            // Thông tin thuộc tính
            private AttributeDTO.GetAttributeDTO attribute;

            // Tổng tiền của dòng sản phẩm
            private BigDecimal lineTotal;
        }
    }
}