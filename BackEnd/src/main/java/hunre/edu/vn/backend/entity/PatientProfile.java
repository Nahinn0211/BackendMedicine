package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patient_profiles", indexes = {
        @Index(name = "idx_patient_user", columnList = "user_id", unique = true),
        @Index(name = "idx_patient_blood_type", columnList = "blood_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PatientProfile extends BaseEntity {
    @NotNull(message = "Người dùng không được trống")
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "blood_type")
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;

    @Column(name = "medical_history", columnDefinition = "nvarchar(MAX)")
    private String medicalHistory;

    @Column(name = "allergies", columnDefinition = "nvarchar(MAX)")
    private String allergies;

    @NotNull(message = "Số dư tài khoản không được trống")
    @DecimalMin(value = "0.0", message = "Số dư tài khoản phải lớn hơn hoặc bằng 0")
    @Column(name = "account_balance", precision = 19, scale = 2)
    private BigDecimal accountBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();

    @Formula("(SELECT COUNT(a.id) FROM appointments a WHERE a.patient_id = id AND a.status = 'COMPLETED')")
    private Integer completedAppointmentsCount;

    @Formula("(SELECT COUNT(c.id) FROM consultations c WHERE c.patient_id = id AND c.status = 'COMPLETED')")
    private Integer completedConsultationsCount;

    public enum BloodType {
        A_POSITIVE, A_NEGATIVE,
        B_POSITIVE, B_NEGATIVE,
        AB_POSITIVE, AB_NEGATIVE,
        O_POSITIVE, O_NEGATIVE
    }

    public void updateAccountBalance(BigDecimal amount) {
        if (amount == null) return;
        this.accountBalance = this.accountBalance.add(amount);
    }

    public void addMedicalHistory(String newMedicalHistory) {
        if (newMedicalHistory == null || newMedicalHistory.trim().isEmpty()) return;

        this.medicalHistory = this.medicalHistory == null
                ? newMedicalHistory
                : this.medicalHistory + "\n" + newMedicalHistory;
    }

    public void addAllergies(String newAllergies) {
        if (newAllergies == null || newAllergies.trim().isEmpty()) return;

        this.allergies = this.allergies == null
                ? newAllergies
                : this.allergies + "\n" + newAllergies;
    }

    public boolean hasAllergies() {
        return this.allergies != null && !this.allergies.trim().isEmpty();
    }

    public boolean hasMedicalHistory() {
        return this.medicalHistory != null && !this.medicalHistory.trim().isEmpty();
    }

    public int getTotalConsultations() {
        return this.completedConsultationsCount != null
                ? this.completedConsultationsCount
                : 0;
    }
}