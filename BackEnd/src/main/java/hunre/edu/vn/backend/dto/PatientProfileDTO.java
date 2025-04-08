package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.PatientProfile;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO Class for PatientProfile
 */
public class PatientProfileDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetPatientProfileDTO {
        private Long id;
        private Long userId;
        private UserDTO.GetUserDTO user;
        private PatientProfile.BloodType bloodType;
        private String medicalHistory;
        private String allergies;
        private BigDecimal accountBalance;
        private Integer completedAppointmentsCount;
        private Integer completedConsultationsCount;
        private Boolean hasMedicalHistory;
        private Boolean hasAllergies;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetPatientProfileDTOWithoutDetails {
        private Long id;
        private Long userId;
        private PatientProfile.BloodType bloodType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SavePatientProfileDTO {
        private Long id;

        @NotNull(message = "ID người dùng không được trống")
        private Long userId;

        private PatientProfile.BloodType bloodType;
        private String medicalHistory;
        private String allergies;
        private BigDecimal accountBalance;
    }

    public static GetPatientProfileDTO fromEntity(PatientProfile patientProfile) {
        if (patientProfile == null) return null;

        return GetPatientProfileDTO.builder()
                .id(patientProfile.getId())
                .createdAt(patientProfile.getCreatedAt())
                .updatedAt(patientProfile.getUpdatedAt())
                .user(UserDTO.fromEntity(patientProfile.getUser()))
                .bloodType(patientProfile.getBloodType())
                .medicalHistory(patientProfile.getMedicalHistory())
                .allergies(patientProfile.getAllergies())
                .accountBalance(patientProfile.getAccountBalance())
                .completedAppointmentsCount(patientProfile.getCompletedAppointmentsCount())
                .completedConsultationsCount(patientProfile.getCompletedConsultationsCount())
                .hasMedicalHistory(patientProfile.hasMedicalHistory())
                .hasAllergies(patientProfile.hasAllergies())
                .build();
    }

    public static GetPatientProfileDTOWithoutDetails fromEntityWithoutDetails(PatientProfile patientProfile) {
        if (patientProfile == null) return null;

        return GetPatientProfileDTOWithoutDetails.builder()
                .id(patientProfile.getId())
                .userId(patientProfile.getUser() != null ? patientProfile.getUser().getId() : null)
                .bloodType(patientProfile.getBloodType())
                .createdAt(patientProfile.getCreatedAt())
                .updatedAt(patientProfile.getUpdatedAt())
                .build();
    }
}