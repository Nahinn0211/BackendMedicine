package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.BookingStatus;
import hunre.edu.vn.backend.entity.PaymentMethod;
import hunre.edu.vn.backend.entity.ServiceBooking;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceBookingDTO {
    private String code;
    private String name;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetServiceBookingDTO {
        private Long id;
        private Long serviceId;
        private Long doctorId;
        private Long patientId;
        private ServiceDTO.GetServiceDTO service;
        private DoctorProfileDTO.GetDoctorProfileDTO doctor;
        private PatientProfileDTO.GetPatientProfileDTO patient;
        private BigDecimal totalPrice;
        private String paymentMethod;
        private String status;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveServiceBookingDTO {
        private Long id; // Optional for update

        @NotBlank(message = "Mã đặt dịch vụ không được trống")
        private String code;

        @NotBlank(message = "Tên đặt dịch vụ không được trống")
        private String name;

        @NotNull(message = "ID dịch vụ không được trống")
        private Long serviceId;

        @NotNull(message = "ID bác sĩ không được trống")
        private Long doctorId;

        @NotNull(message = "ID bệnh nhân không được trống")
        private Long patientId;

        private BigDecimal totalPrice;
        private String paymentMethod;
        private String status;
        private String notes;
    }

    // Static method to convert Entity to DTO
    public static GetServiceBookingDTO fromEntity(ServiceBooking serviceBooking) {
        if (serviceBooking == null) return null;

        return GetServiceBookingDTO.builder()
                .id(serviceBooking.getId())
                .service(ServiceDTO.fromEntity(serviceBooking.getService()))
                .doctor(DoctorProfileDTO.fromEntity(serviceBooking.getDoctor()))
                .patient(PatientProfileDTO.fromEntity(serviceBooking.getPatient()))
                .totalPrice(serviceBooking.getTotalPrice())
                .paymentMethod(serviceBooking.getPaymentMethod() != null ?
                        serviceBooking.getPaymentMethod().name() : null)
                .status(serviceBooking.getStatus() != null ?
                        serviceBooking.getStatus().name() : null)
                .notes(serviceBooking.getNotes())
                .createdAt(serviceBooking.getCreatedAt())
                .updatedAt(serviceBooking.getUpdatedAt())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailedServiceBookingDto {
        private Long id;
        private Long serviceId;
        private Long doctorId;
        private Long patientId;
        private BigDecimal totalPrice;
        private PaymentMethod paymentMethod;
        private BookingStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String serviceName;
        private String patientName;
        private String doctorName;
        private String patientEmail;
        private String doctorEmail;
    }
}