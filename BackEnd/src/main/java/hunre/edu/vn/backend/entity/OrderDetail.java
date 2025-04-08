package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "order_details", indexes = {
        @Index(name = "idx_order_detail_order", columnList = "order_id"),
        @Index(name = "idx_order_detail_medicine", columnList = "medicine_id"),
        @Index(name = "idx_order_detail_attribute", columnList = "attribute_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class OrderDetail extends BaseEntity {
    @NotNull(message = "Đơn hàng không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull(message = "Thuốc không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @NotNull(message = "Thuộc tính thuốc không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @NotNull(message = "Số lượng không được trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    @Max(value = 1000, message = "Số lượng không được vượt quá 1000")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull(message = "Đơn giá không được trống")
    @DecimalMin(value = "0.0", message = "Đơn giá phải lớn hơn hoặc bằng 0")
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    @DecimalMin(value = "0.0", message = "Số tiền giảm giá phải lớn hơn hoặc bằng 0")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_price", precision = 19, scale = 2)
    private BigDecimal totalPrice;

    public BigDecimal calculateTotalPrice() {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal grossTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal netTotal = grossTotal.subtract(
                discountAmount != null ? discountAmount : BigDecimal.ZERO
        );

        this.totalPrice = netTotal.setScale(2, RoundingMode.HALF_UP);
        return this.totalPrice;
    }


    public void applyDiscount(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải từ 0-100");
        }

        BigDecimal grossTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.discountAmount = grossTotal
                .multiply(BigDecimal.valueOf(discountPercentage / 100))
                .setScale(2, RoundingMode.HALF_UP);

        calculateTotalPrice();
    }

    public boolean checkAvailability() {
        return attribute != null &&
                attribute.getStock() >= quantity;
    }

    public void updateInventory() {
        if (attribute != null && checkAvailability()) {
            attribute.setStock(attribute.getStock() - quantity);
        }
    }

    public String getProductDetails() {
        return String.format(
                "Thuốc: %s (Mã: %s), Số lượng: %d, Đơn giá: %s",
                medicine.getName(),
                medicine.getCode(),
                quantity,
                unitPrice.toString()
        );
    }
}