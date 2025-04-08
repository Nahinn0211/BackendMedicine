package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vouchers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_voucher_code", columnNames = {"code"})
        },
        indexes = {
                @Index(name = "idx_voucher_code", columnList = "code"),
                @Index(name = "idx_voucher_start_date", columnList = "start_date"),
                @Index(name = "idx_voucher_end_date", columnList = "end_date"),
                @Index(name = "idx_voucher_status", columnList = "status")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Voucher extends BaseEntity {
    @NotBlank(message = "Mã voucher không được để trống")
    @Size(min = 4, max = 50, message = "Mã voucher từ 4 đến 50 ký tự")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Mã voucher chỉ được chứa chữ in hoa, số và dấu gạch ngang")
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @NotNull(message = "Phần trăm giảm giá không được để trống")
    @DecimalMin(value = "0.0", message = "Phần trăm giảm giá phải lớn hơn hoặc bằng 0")
    @DecimalMax(value = "100.0", message = "Phần trăm giảm giá không được vượt quá 100")
    @Column(name = "voucher_percentage", nullable = false)
    private Double voucherPercentage;

    @NotNull(message = "Số lượng voucher không được để trống")
    @Min(value = 0, message = "Số lượng voucher phải lớn hơn hoặc bằng 0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Future(message = "Ngày kết thúc phải ở tương lai")
    @Column(name = "end_date", nullable = true)
    private LocalDateTime endDate;

    @NotNull(message = "Giá trị tối thiểu không được để trống")
    @DecimalMin(value = "0.0", message = "Giá trị tối thiểu phải lớn hơn hoặc bằng 0")
    @Column(name = "minimum_order_value", nullable = false)
    private BigDecimal minimumOrderValue;

    @NotNull(message = "Trạng thái voucher không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VoucherStatus status;

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();

        if (status != VoucherStatus.ACTIVE) {
            return false;
        }

        if (now.isBefore(startDate)) {
            return false;
        }

        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }

        return stock > 0;
    }

    public void decreaseStock() {
        if (stock > 0) {
            stock--;
        } else {
            throw new IllegalStateException("Voucher đã hết số lượng");
        }
    }

    public void increaseStock(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Số lượng tăng phải là số dương");
        }
        stock += amount;
    }

    public BigDecimal calculateDiscountAmount(BigDecimal originalPrice) {
        if (originalPrice == null || originalPrice.compareTo(minimumOrderValue) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = originalPrice
                .multiply(BigDecimal.valueOf(voucherPercentage / 100))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        return discountAmount;
    }

    public enum VoucherStatus {
        ACTIVE,
        EXPIRED,
        DISABLED
    }
}