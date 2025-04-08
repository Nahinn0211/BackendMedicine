package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Appointment;
import hunre.edu.vn.backend.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * DTO Class for Appointment
 */
public class AppointmentDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAppointmentDTO {
        private Long id;
        private Long patientId;
        private Long serviceBookingId;
        private Long doctorId;
        private PatientProfileDTO.GetPatientProfileDTO patient;
        private ServiceBookingDTO.GetServiceBookingDTO serviceBooking;
        private DoctorProfileDTO.GetDoctorProfileDTO doctor;
        private ConsultationDTO.GetConsultationDTO consultation;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private AppointmentStatus status;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAppointmentDTOWithoutDetails {
        private Long id;
        private Long patientId;
        private String patientName;
        private Long serviceBookingId;
        private String serviceName;
        private Long doctorId;
        private String doctorName;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private AppointmentStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveAppointmentDTO {
        private Long id; // Optional for update

        @NotNull(message = "Bệnh nhân không được trống")
        private Long patientId;

        @NotNull(message = "Dịch vụ đặt không được trống")
        private Long serviceBookingId;

        @NotNull(message = "Bác sĩ không được trống")
        private Long doctorId;

        @NotNull(message = "Ngày đặt lịch không được trống")
        private LocalDate appointmentDate;

        @NotNull(message = "Giờ đặt lịch không được trống")
        private LocalTime appointmentTime;

        private AppointmentStatus status;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppointmentStatsDTO {
        private Long todayAppointments;
        private Long weeklyAppointments;
        private Long monthlyAppointments;
        private Long totalAppointments;
        private Map<LocalDate, Long> weeklyStats;
        private Map<Integer, Long> monthlyStats;
    }

    public static GetAppointmentDTO fromEntity(Appointment appointment) {
        if (appointment == null) return null;

        return GetAppointmentDTO.builder()
                .id(appointment.getId())
                .patient(appointment.getPatient() != null ?
                        PatientProfileDTO.fromEntity(appointment.getPatient()) : null)
                .serviceBooking(appointment.getServiceBooking() != null ?
                        ServiceBookingDTO.fromEntity(appointment.getServiceBooking()) : null)
                .doctor(appointment.getDoctor() != null ?
                        DoctorProfileDTO.fromEntity(appointment.getDoctor()) : null)
                .consultation(appointment.getConsultation() != null ?
                        ConsultationDTO.fromEntity(appointment.getConsultation()) : null)
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                 .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    public static GetAppointmentDTOWithoutDetails fromEntityWithoutDetails(Appointment appointment) {
        if (appointment == null) return null;

        return GetAppointmentDTOWithoutDetails.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient() != null ?
                        appointment.getPatient().getId() : null)
                .patientName(appointment.getPatient() != null ?
                        appointment.getPatient().getUser().getFullName() : null)
                .serviceBookingId(appointment.getServiceBooking() != null ?
                        appointment.getServiceBooking().getId() : null)
                .serviceName(appointment.getServiceBooking() != null ?
                        appointment.getServiceBooking().getService().getName() : null)
                .doctorId(appointment.getDoctor() != null ?
                        appointment.getDoctor().getId() : null)
                .doctorName(appointment.getDoctor() != null ?
                        appointment.getDoctor().getUser().getFullName() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppointmentDetailsDTO {
        private Long id;
        private Long patientId;
        private Long doctorId;
        private String patientName;
        private String doctorName;
        private String patientEmail;
        private String doctorEmail;
        private String patientPhone;
        private String doctorPhone;
        private LocalTime appointmentTime;
        private LocalDate appointmentDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}