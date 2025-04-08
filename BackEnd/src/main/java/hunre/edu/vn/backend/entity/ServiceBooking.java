package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_bookings", indexes = {
        @Index(name = "idx_service_booking_service", columnList = "service_id"),
        @Index(name = "idx_service_booking_doctor", columnList = "doctor_id"),
        @Index(name = "idx_service_booking_patient", columnList = "patient_id"),
        @Index(name = "idx_service_booking_status", columnList = "status"),
        @Index(name = "idx_service_booking_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ServiceBooking extends BaseEntity {
    @NotNull(message = "Dịch vụ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @NotNull(message = "Bác sĩ không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @OneToOne(mappedBy = "serviceBooking",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = true)
    @ToString.Exclude
    private Appointment appointment;

    @NotNull(message = "Bệnh nhân không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @NotNull(message = "Tổng giá không được để trống")
    @DecimalMin(value = "0.0", message = "Tổng giá phải là số dương")
    @Digits(integer = 10, fraction = 2, message = "Tổng giá vượt quá giới hạn cho phép")
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @NotNull(message = "Trạng thái đặt dịch vụ không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    @Column(name = "notes", columnDefinition = "nvarchar(MAX)")
    private String notes;

    public void updateStatus(BookingStatus newStatus) {
        this.status = newStatus;
    }

    public void addNotes(String additionalNotes) {
        if (additionalNotes == null || additionalNotes.trim().isEmpty()) {
            return;
        }

        this.notes = this.notes == null
                ? additionalNotes
                : this.notes + "\n" + additionalNotes;
    }

    public boolean isCancellable() {
        return this.status == BookingStatus.PENDING;
    }

    public void cancel() {
        if (isCancellable()) {
            this.status = BookingStatus.CANCELLED;
            addNotes("Hủy đặt lịch");
        } else {
            throw new IllegalStateException("Không thể hủy đặt dịch vụ ở trạng thái hiện tại");
        }
    }
}