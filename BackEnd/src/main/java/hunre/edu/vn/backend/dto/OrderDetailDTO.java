package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.MedicineMedia;
import hunre.edu.vn.backend.entity.OrderDetail;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private MedicineDTO medicine;
    private AttributeDTO attribute;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetOrderDetailDTO {
        private Long id;
        private Long orderId;
        private Long medicineId;
        private Long attributeId;
        private MedicineDTO.GetMedicineDTO medicine;
        private AttributeDTO.GetAttributeDTO attribute;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discountAmount;
        private BigDecimal totalPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveOrderDetailDTO {
        private Long id; // Optional for update

        @NotNull(message = "ID đơn hàng không được trống")
        private Long orderId;

        @NotNull(message = "ID thuốc không được trống")
        private Long medicineId;

        private Long attributeId;

        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        @NotNull(message = "Giá đơn vị không được trống")
        private BigDecimal unitPrice;

        private BigDecimal discountAmount;
    }

    // Static method to convert Entity to DTO
    public static GetOrderDetailDTO fromEntity(OrderDetail orderDetail) {
        if (orderDetail == null) {
            return null;
        }

        // Tạo DTO với các thông tin cơ bản
        GetOrderDetailDTO dto = GetOrderDetailDTO.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder() != null ? orderDetail.getOrder().getId() : null)
                .medicineId(orderDetail.getMedicine() != null ? orderDetail.getMedicine().getId() : null)
                .attributeId(orderDetail.getAttribute() != null ? orderDetail.getAttribute().getId() : null)
                .quantity(orderDetail.getQuantity())
                .unitPrice(orderDetail.getUnitPrice())
                .discountAmount(orderDetail.getDiscountAmount())
                .totalPrice(orderDetail.getTotalPrice())
                .createdAt(orderDetail.getCreatedAt())
                .updatedAt(orderDetail.getUpdatedAt())
                .build();

        // Xử lý Medicine thủ công
        if (orderDetail.getMedicine() != null) {
            // Lấy Medicine DTO từ entity
            MedicineDTO.GetMedicineDTO medicineDTO = MedicineDTO.fromEntity(orderDetail.getMedicine());
            dto.setMedicine(medicineDTO);
        }

        // Xử lý Attribute thủ công
        if (orderDetail.getAttribute() != null) {
            dto.setAttribute(AttributeDTO.fromEntity(orderDetail.getAttribute()));
        }

        return dto;
    }
}