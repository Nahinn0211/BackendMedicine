package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services",
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_service_name", columnNames = {"name"})
        },
        indexes = {
                @Index(name = "idx_service_name", columnList = "name"),
                @Index(name = "idx_service_price", columnList = "price")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Service extends BaseEntity {
    @NotBlank(message = "Tên dịch vụ không được trống")
    @Size(min = 2, max = 255, message = "Tên dịch vụ từ 2 đến 255 ký tự")
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(255)")
    private String name;

    @NotBlank(message = "Ảnh dịch vụ không được trống")
    @Column(name = "image", nullable = false)
    private String image;

    @NotNull(message = "Giá dịch vụ không được trống")
    @DecimalMin(value = "0.0", message = "Giá phải lớn hơn 0")
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Size(max = 1000, message = "Mô tả dịch vụ ít hơn 1000 kí tự")
    @Column(name = "description", nullable = true, columnDefinition = "nvarchar(MAX)")
    private String description;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "service",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<DoctorService> doctorServices = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "service",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<ServiceBooking> serviceBookings = new ArrayList<>();

    public void addDoctorService(DoctorService doctorService) {
        if (doctorService != null) {
            this.doctorServices.add(doctorService);
            doctorService.setService(this);
        }
    }

    public void removeDoctorService(DoctorService doctorService) {
        if (doctorService != null) {
            this.doctorServices.remove(doctorService);
            doctorService.setService(null);
        }
    }

    public void addServiceBooking(ServiceBooking serviceBooking) {
        if (serviceBooking != null) {
            this.serviceBookings.add(serviceBooking);
            serviceBooking.setService(this);
        }
    }

    public void removeServiceBooking(ServiceBooking serviceBooking) {
        if (serviceBooking != null) {
            this.serviceBookings.remove(serviceBooking);
            serviceBooking.setService(null);
        }
    }
}