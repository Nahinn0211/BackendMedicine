package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts", indexes = {
        @Index(name = "idx_discount_code", columnList = "code", unique = true),
        @Index(name = "idx_discount_medicine", columnList = "medicine_id"),
        @Index(name = "idx_discount_date", columnList = "start_date,end_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Discount extends BaseEntity {
    @NotBlank(message = "Mã giảm giá không được để trống")
    @Pattern(regexp = "^[A-Z0-9]{6,10}$", message = "Mã giảm giá phải từ 6-10 ký tự và chỉ gồm chữ hoa và số")
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotBlank(message = "Tên giảm giá không được để trống")
    @Size(min = 3, max = 255, message = "Tên giảm giá phải từ 3-255 ký tự")
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(255)")
    private String name;

    @NotNull(message = "Thuốc áp dụng không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @NotNull(message = "Phần trăm giảm giá không được trống")
    @DecimalMin(value = "0.0", message = "Phần trăm giảm giá phải lớn hơn hoặc bằng 0")
    @DecimalMax(value = "100.0", message = "Phần trăm giảm giá không được vượt quá 100")
    @Column(name = "discount_percentage", nullable = false)
    private Double discountPercentage;

    @NotNull(message = "Ngày bắt đầu không được trống")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Future(message = "Ngày kết thúc phải là ngày trong tương lai")
    @Column(name = "end_date", nullable = true)
    private LocalDateTime endDate;

    @Column(name = "description", columnDefinition = "nvarchar(MAX)")
    private String description;

    @Column(name = "max_discount_amount", precision = 19, scale = 2)
    @DecimalMin(value = "0.0", message = "Số tiền giảm tối đa phải lớn hơn hoặc bằng 0")
    private BigDecimal maxDiscountAmount;

    @Column(name = "min_purchase_amount", precision = 19, scale = 2)
    @DecimalMin(value = "0.0", message = "Số tiền mua tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal minPurchaseAmount;

    @Column(name = "usage_limit")
    @Min(value = 0, message = "Giới hạn sử dụng phải lớn hơn hoặc bằng 0")
    private Integer usageLimit;

    @Column(name = "current_usage_count")
    @Min(value = 0, message = "Số lần sử dụng hiện tại phải lớn hơn hoặc bằng 0")
    private Integer currentUsageCount = 0;

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        boolean withinDateRange = now.isAfter(startDate) &&
                (endDate == null || now.isBefore(endDate));

        boolean withinUsageLimit = usageLimit == null ||
                currentUsageCount < usageLimit;

        return withinDateRange && withinUsageLimit;
    }

    public BigDecimal calculateDiscountAmount(BigDecimal originalPrice) {
        if (!isValid()) return BigDecimal.ZERO;

        BigDecimal discountAmount = originalPrice
                .multiply(BigDecimal.valueOf(discountPercentage / 100));

        // Áp dụng giới hạn số tiền giảm nếu có
        if (maxDiscountAmount != null) {
            discountAmount = discountAmount.min(maxDiscountAmount);
        }

        return discountAmount;
    }

    public void incrementUsage() {
        if (currentUsageCount == null) {
            currentUsageCount = 0;
        }
        currentUsageCount++;
    }

    public boolean canApplyToOrder(BigDecimal orderTotal) {
        return isValid() &&
                (minPurchaseAmount == null ||
                        orderTotal.compareTo(minPurchaseAmount) >= 0);
    }
}