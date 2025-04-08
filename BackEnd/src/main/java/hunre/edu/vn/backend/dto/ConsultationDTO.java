package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Consultation;
import hunre.edu.vn.backend.entity.ConsultationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationDTO {
    private String consultationCode;
    private String consultationLink;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetConsultationDTO {
        private Long id;
        private Long patientId;
        private String patientName;
        private Long doctorId;
        private String doctorName;
        private Long appointmentId;
        private String consultationLink;
        private String consultationCode;
        private ConsultationStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private Boolean isVideoEnabled;
        private String sessionToken;
        private String rtcSessionId;
        private List<ChatMessageDTO.GetChatMessageDTO> recentMessages;

        // Related entities DTOs
        private PatientProfileDTO.GetPatientProfileDTO patient;
        private DoctorProfileDTO.GetDoctorProfileDTO doctor;
        private AppointmentDTO.GetAppointmentDTO appointment;
        private List<ChatMessageDTO.GetChatMessageDTO> chatMessages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveConsultationDTO {
        private Long id; // Optional for update

        @NotNull(message = "Mã tư vấn không được trống")
        private String consultationCode;

        private String consultationLink;
        private ConsultationStatus status;

        @NotNull(message = "ID bệnh nhân không được trống")
        private Long patientId;

        @NotNull(message = "ID bác sĩ không được trống")
        private Long doctorId;

        private Long appointmentId;
    }

    // Static method to convert Entity to DTO
    public static GetConsultationDTO fromEntity(Consultation consultation) {
        if (consultation == null) {
            return null;
        }

        return GetConsultationDTO.builder()
                .id(consultation.getId())
                .consultationCode(consultation.getConsultationCode())
                .consultationLink(consultation.getConsultationLink())
                .status(consultation.getStatus())
                .startedAt(consultation.getStartedAt())
                .endedAt(consultation.getEndedAt())
                .isVideoEnabled(consultation.getIsVideoEnabled())
                .createdAt(consultation.getCreatedAt())
                .updatedAt(consultation.getUpdatedAt())
                .patient(PatientProfileDTO.fromEntity(consultation.getPatient()))
                .doctor(DoctorProfileDTO.fromEntity(consultation.getDoctor()))
                .appointment(AppointmentDTO.fromEntity(consultation.getAppointment()))
                .chatMessages(consultation.getChatMessages() != null
                        ? consultation.getChatMessages().stream()
                        .map(ChatMessageDTO::fromEntity)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StartSessionDto {
        private Long consultationId;
        private Long initiatorId;
        private String initiatorType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EndSessionDto {
        private Long consultationId;
        private Long terminatorId;
        private String terminatorType;
        private String terminationReason;
    }
}