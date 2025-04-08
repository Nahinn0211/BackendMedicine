package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.DoctorService;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorServiceDTO {
    private ServiceDTO service;
    private DoctorProfileDTO doctor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetDoctorServiceDTO {
        private Long id;
        private Long serviceId;
        private Long doctorId;
        private ServiceDTO.GetServiceDTO service;
        private DoctorProfileDTO.GetDoctorProfileDTO doctor;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveDoctorServiceDTO {
        private Long id; // Optional for update

        @NotNull(message = "ID dịch vụ không được trống")
        private Long serviceId;

        @NotNull(message = "ID bác sĩ không được trống")
        private Long doctorId;
    }

    // Static method to convert Entity to DTO
    public static GetDoctorServiceDTO fromEntity(DoctorService doctorService) {
        if (doctorService == null) {
            return null;
        }

        return GetDoctorServiceDTO.builder()
                .id(doctorService.getId())
                .service(ServiceDTO.fromEntity(doctorService.getService()))
                .doctor(DoctorProfileDTO.fromEntity(doctorService.getDoctor()))
                .createdAt(doctorService.getCreatedAt())
                .updatedAt(doctorService.getUpdatedAt())
                .build();
    }
}