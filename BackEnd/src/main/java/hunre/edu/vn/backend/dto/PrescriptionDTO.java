package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Prescription;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO Class for Prescription
 */
public class PrescriptionDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetPrescriptionDTO {
        private Long id;
        private Long patientId;
        private Long doctorId;
        private Long appointmentId;
        private Long medicineId;
        private MedicineDTO.GetMedicineDTO medicine;
        private String dosage;
        private LocalDateTime prescriptionDate;
        private LocalDate expiryDate;
        private String notes;
        private Prescription.PrescriptionStatus status;
        private Boolean isValid;
        private Boolean isNearingExpiry;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SavePrescriptionDTO {
        private Long id; // Optional for update

        @NotNull(message = "ID bác sĩ không được trống")
        private Long doctorId;

        @NotNull(message = "ID bệnh nhân không được trống")
        private Long patientId;

        private Long appointmentId;

        @NotNull(message = "ID thuốc không được trống")
        private Long medicineId;

        @NotNull(message = "Liều lượng không được trống")
        private String dosage;

        private LocalDate expiryDate;
        private String notes;
        private Prescription.PrescriptionStatus status;
    }

    public static GetPrescriptionDTO fromEntity(Prescription prescription) {
        if (prescription == null) return null;

        return GetPrescriptionDTO.builder()
                .id(prescription.getId())
                .medicineId(prescription.getMedicine() != null ?
                        prescription.getMedicine().getId() : null)
                .dosage(prescription.getDosage())
                .prescriptionDate(prescription.getPrescriptionDate())
                .expiryDate(prescription.getExpiryDate())
                .notes(prescription.getNotes())
                .status(prescription.getStatus())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }

    public static List<GetPrescriptionDTO> fromEntity(List<Prescription> prescriptions) {
        if (prescriptions == null) return null;
        return prescriptions.stream()
                .map(PrescriptionDTO::fromEntity)
                .collect(Collectors.toList());
    }
}