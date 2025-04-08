package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_code", columnList = "order_code", unique = true),
        @Index(name = "idx_order_patient", columnList = "patient_id"),
        @Index(name = "idx_order_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {
    @NotBlank(message = "Mã đơn hàng không được để trống")
    @Pattern(regexp = "^ORD-\\d{6}$", message = "Mã đơn hàng phải có định dạng ORD-XXXXXX")
    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;

    @NotNull(message = "Bệnh nhân không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @NotNull(message = "Tổng tiền không được trống")
    @DecimalMin(value = "0.0", message = "Tổng tiền phải lớn hơn hoặc bằng 0")
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @NotNull(message = "Phương thức thanh toán không được trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @NotNull(message = "Trạng thái đơn hàng không được trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "voucher_code", nullable = true)
    private String voucherCode;

    @Column(name = "discount_amount", nullable = true)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "note", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String note;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_reason", columnDefinition = "nvarchar(MAX)")
    private String cancelledReason;

    @Formula("(SELECT COUNT(od.id) FROM order_details od WHERE od.order_id = id)")
    private Integer itemCount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void complete() {
        this.status = OrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.COMPLETED;
    }

    public void cancel(String reason) {
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledReason = reason;

        if (this.paymentStatus == PaymentStatus.COMPLETED && this.paymentMethod != PaymentMethod.CASH) {
            this.patient.updateAccountBalance(this.totalPrice);
        }
    }

    public void process() {
        this.status = OrderStatus.CONFIRMED;
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
