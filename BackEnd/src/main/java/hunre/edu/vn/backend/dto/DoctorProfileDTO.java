package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.DoctorProfile;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorProfileDTO {
    private String specialization;
    private String workplace;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetDoctorProfileDTO {
        private Long id;
        private Long userId;
        private UserDTO.GetUserDTO user;
        private String experience;
        private String specialization;
        private String workplace;
        private BigDecimal accountBalance;
        private String certifications;
        private String biography;
        private LocalDateTime availableFrom;
        private LocalDateTime availableTo;
        private Boolean isAvailable;
        private Integer uniquePatientCount;
        private Integer totalConsultationCount;
        private Double averageRating;
        private List<ReviewDTO.GetReviewDTO> reviews;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveDoctorProfileDTO {
        private Long id; // Optional for update

        @NotNull(message = "ID người dùng không được trống")
        private Long userId;

        private String experience;

        @NotNull(message = "Chuyên khoa không được trống")
        private String specialization;

        private String workplace;
        private BigDecimal accountBalance;
        private String certifications;
        private String biography;
        private LocalDateTime availableFrom;
        private LocalDateTime availableTo;
        private Boolean isAvailable;
        private List<Long> serviceIds;
    }

    // Static method to convert Entity to DTO
    public static GetDoctorProfileDTO fromEntity(DoctorProfile doctorProfile) {
        if (doctorProfile == null) return null;

        GetDoctorProfileDTO dto = GetDoctorProfileDTO.builder()
                .id(doctorProfile.getId())
                .createdAt(doctorProfile.getCreatedAt())
                .updatedAt(doctorProfile.getUpdatedAt())
                .user(UserDTO.fromEntity(doctorProfile.getUser()))
                .experience(doctorProfile.getExperience())
                .specialization(doctorProfile.getSpecialization())
                .workplace(doctorProfile.getWorkplace())
                .accountBalance(doctorProfile.getAccountBalance())
                .certifications(doctorProfile.getCertifications())
                .biography(doctorProfile.getBiography())
                .availableFrom(doctorProfile.getAvailableFrom())
                .availableTo(doctorProfile.getAvailableTo())
                .isAvailable(doctorProfile.getIsAvailable())
                .uniquePatientCount(doctorProfile.getUniquePatientCount())
                .totalConsultationCount(doctorProfile.getTotalConsultationCount())
                .averageRating(doctorProfile.getAverageRating())
                .build();

        // Set reviews
        if (doctorProfile.getReviews() != null) {
            dto.setReviews(doctorProfile.getReviews().stream()
                    .filter(review -> !review.getIsDeleted())
                    .map(ReviewDTO::fromEntity)
                    .toList());
        }

        return dto;
    }

    public static GetDoctorProfileDTO fromEntityWithServices(DoctorProfile doctorProfile) {
        if (doctorProfile == null) return null;

        GetDoctorProfileDTO dto = fromEntity(doctorProfile);
        return dto;
    }
}