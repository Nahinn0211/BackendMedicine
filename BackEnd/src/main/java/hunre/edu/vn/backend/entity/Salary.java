package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salaries", indexes = {
        @Index(name = "idx_salary_user", columnList = "user_id"),
        @Index(name = "idx_salary_status", columnList = "status"),
        @Index(name = "idx_salary_payment_date", columnList = "payment_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Salary extends BaseEntity {
    @NotNull(message = "Người dùng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Mã ngân hàng không được để trống")
    @Pattern(regexp = "^[A-Z0-9]{6,20}$", message = "Mã ngân hàng phải từ 6-20 ký tự và chỉ chứa chữ in hoa và số")
    @Column(name = "bank_code", nullable = false, length = 20)
    private String bankCode;

    @NotBlank(message = "Tên ngân hàng không được để trống")
    @Size(min = 2, max = 255, message = "Tên ngân hàng từ 2 đến 255 ký tự")
    @Column(name = "bank_name", nullable = false, columnDefinition = "nvarchar(255)")
    private String bankName;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", message = "Số tiền phải lớn hơn hoặc bằng 0")
    @Digits(integer = 12, fraction = 2, message = "Số tiền không hợp lệ")
    @Column(name = "price", nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Trạng thái thanh toán không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "note", columnDefinition = "nvarchar(MAX)")
    private String note;

    public void updateStatus(PaymentStatus newStatus) {
        this.status = newStatus;
    }

    public void addNote(String additionalNote) {
        if (additionalNote == null || additionalNote.trim().isEmpty()) {
            return;
        }

        this.note = this.note == null
                ? additionalNote
                : this.note + "\n" + additionalNote;
    }

    public boolean isPayable() {
        return this.status == PaymentStatus.PENDING;
    }

    public void markAsPaid() {
        if (isPayable()) {
            this.status = PaymentStatus.COMPLETED;
            this.paymentDate = LocalDate.now();
        } else {
            throw new IllegalStateException("Không thể thanh toán lương ở trạng thái hiện tại");
        }
    }
}