package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "doctor_services", indexes = {
        @Index(name = "idx_doctor_service_unique", columnList = "doctor_id,service_id", unique = true),
        @Index(name = "idx_doctor_service_doctor", columnList = "doctor_id"),
        @Index(name = "idx_doctor_service_service", columnList = "service_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class DoctorService extends BaseEntity {
    @NotNull(message = "Dịch vụ không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @NotNull(message = "Bác sĩ không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorProfile doctor;
}