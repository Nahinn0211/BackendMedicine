package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctor_profiles", indexes = {
        @Index(name = "idx_doctor_user", columnList = "user_id", unique = true),
        @Index(name = "idx_doctor_specialization", columnList = "specialization"),
        @Index(name = "idx_doctor_workplace", columnList = "workplace")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class DoctorProfile extends BaseEntity {
    @NotNull(message = "Người dùng không được trống")
    @OneToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "Kinh nghiệm không được để trống")
    @Size(min = 10, max = 2000, message = "Kinh nghiệm phải từ 10-2000 ký tự")
    @Column(name = "experience", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String experience;

    @NotBlank(message = "Chuyên môn không được để trống")
    @Size(min = 3, max = 100, message = "Chuyên môn phải từ 3-100 ký tự")
    @Column(name = "specialization", nullable = false, columnDefinition = "nvarchar(255)")
    private String specialization;

    @NotBlank(message = "Nơi làm việc không được để trống")
    @Size(min = 3, max = 255, message = "Nơi làm việc phải từ 3-255 ký tự")
    @Column(name = "workplace", nullable = false, columnDefinition = "nvarchar(255)")
    private String workplace;

    @NotNull(message = "Số dư tài khoản không được trống")
    @DecimalMin(value = "0.0", message = "Số dư tài khoản phải lớn hơn hoặc bằng 0")
    @Column(name = "account_balance", precision = 19, scale = 2)
    private BigDecimal accountBalance = BigDecimal.ZERO;

    @Column(name = "certifications", columnDefinition = "nvarchar(MAX)")
    private String certifications;

    @Column(name = "biography", columnDefinition = "nvarchar(MAX)")
    private String biography;

    @Column(name = "available_from")
    private LocalDateTime availableFrom;

    @Column(name = "available_to")
    private LocalDateTime availableTo;

    @Column(name = "is_available", nullable = true)
    private Boolean isAvailable = true;

    @Formula("(SELECT COUNT(DISTINCT c.patient_id) FROM consultations c WHERE c.doctor_id = id AND c.status = 'COMPLETED')")
    private Integer uniquePatientCount;

    @Formula("(SELECT COUNT(c.id) FROM consultations c WHERE c.doctor_id = id AND c.status = 'COMPLETED')")
    private Integer totalConsultationCount;

    @Formula("(SELECT COALESCE(AVG(r.rating), 0) FROM reviews r WHERE r.doctor_id = id)")
    private Double averageRating;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<DoctorService> doctorServices = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    public void addService(Service service) {
        DoctorService doctorService = new DoctorService();
        doctorService.setDoctor(this);
        doctorService.setService(service);
        this.doctorServices.add(doctorService);
    }

    public void removeService(Service service) {
        this.doctorServices.removeIf(ds ->
                ds.getService().getId().equals(service.getId())
        );
    }

    public boolean checkAvailability() {
        if (!Boolean.TRUE.equals(this.isAvailable)) return false;

        LocalDateTime now = LocalDateTime.now();
        return (availableFrom == null || now.isAfter(availableFrom)) &&
                (availableTo == null || now.isBefore(availableTo));
    }

    public void updateAvailability(LocalDateTime from, LocalDateTime to) {
        this.availableFrom = from;
        this.availableTo = to;
        this.isAvailable = true;
    }

    public void updateAccountBalance(BigDecimal amount) {
        this.accountBalance = this.accountBalance.add(amount);
    }

    public int getReviewCount() {
        return this.reviews != null ? this.reviews.size() : 0;
    }
}