package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions", indexes = {
        @Index(name = "idx_prescription_doctor", columnList = "doctor_id"),
        @Index(name = "idx_prescription_patient", columnList = "patient_id"),
        @Index(name = "idx_prescription_medicine", columnList = "medicine_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Prescription extends BaseEntity {
    @NotNull(message = "Bác sĩ không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;

    @NotNull(message = "Bệnh nhân không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @NotNull(message = "Lịch hẹn không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @NotNull(message = "Thuốc không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @NotBlank(message = "Liều lượng không được để trống")
    @Size(min = 5, max = 500, message = "Liều lượng phải từ 5-500 ký tự")
    @Column(name = "dosage", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String dosage;

    @Column(name = "prescription_date")
    private LocalDateTime prescriptionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "notes", columnDefinition = "nvarchar(MAX)")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    public enum PrescriptionStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    public boolean isValid() {
        if (status != PrescriptionStatus.ACTIVE) return false;

        LocalDate today = LocalDate.now();
        return expiryDate == null || !today.isAfter(expiryDate);
    }

    public void complete() {
        this.status = PrescriptionStatus.COMPLETED;
    }

    public void cancel(String cancellationReason) {
        this.status = PrescriptionStatus.CANCELLED;
        this.notes = (this.notes != null ? this.notes + "\n" : "") +
                "Hủy đơn: " + cancellationReason;
    }

    public void addNotes(String additionalNotes) {
        if (additionalNotes == null || additionalNotes.trim().isEmpty()) return;

        this.notes = this.notes == null
                ? additionalNotes
                : this.notes + "\n" + additionalNotes;
    }

    public boolean isNearingExpiry() {
        if (expiryDate == null) return false;

        LocalDate today = LocalDate.now();
        LocalDate nearingExpiryDate = today.plusDays(7);

        return !today.isAfter(expiryDate) &&
                !today.isAfter(nearingExpiryDate);
    }
}