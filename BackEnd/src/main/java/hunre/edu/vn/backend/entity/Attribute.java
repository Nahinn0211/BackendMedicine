package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Attribute extends BaseEntity {
    @NotNull(message = "Thuốc không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @NotBlank(message = "Tên thuộc tính không được để trống")
    @Size(min = 3, max = 255, message = "Tên thuộc tính phải từ 3-255 ký tự")
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(255)")
    private String name;

    @NotNull(message = "Giá nhập không được trống")
    @DecimalMin(value = "0.0", message = "Giá nhập phải lớn hơn hoặc bằng 0")
    @Column(name = "price_in", nullable = false)
    private BigDecimal priceIn;

    @NotNull(message = "Giá bán không được trống")
    @DecimalMin(value = "0.0", message = "Giá bán phải lớn hơn hoặc bằng 0")
    @Column(name = "price_out", nullable = false)
    private BigDecimal priceOut;

    @NotNull(message = "Số lượng tồn không được trống")
    @Min(value = 0, message = "Số lượng tồn phải lớn hơn hoặc bằng 0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @NotNull(message = "Ngày hết hạn không được trống")
    @Future(message = "Ngày hết hạn phải là ngày trong tương lai")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expiryDate);
    }

    public boolean isNearExpiry() {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return this.expiryDate.isBefore(thirtyDaysFromNow) && !isExpired();
    }
}
