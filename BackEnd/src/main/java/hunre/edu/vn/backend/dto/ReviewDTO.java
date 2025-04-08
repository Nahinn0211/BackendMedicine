package hunre.edu.vn.backend.dto;

import hunre.edu.vn.backend.entity.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO Class for Review
 */
public class ReviewDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetReviewDTO {
        private Long id;
        private Long userId;
        private UserDTO.GetUserDTO user;
        private Integer rating;
        private String comment;
        private Long doctorId;
        private Long medicineId;
        private Boolean isPositiveReview;
        private Boolean isNegativeReview;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetReviewDTOWithoutDetails {
        private Long id;
        private Long userId;
        private Long doctorId;
        private Long medicineId;
        private Integer rating;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveReviewDTO {
        private Long id;

        @NotNull(message = "ID người dùng không được trống")
        private Long userId;

        @NotNull(message = "Đánh giá không được trống")
        @Min(value = 1, message = "Đánh giá phải từ 1 đến 5")
        @Max(value = 5, message = "Đánh giá phải từ 1 đến 5")
        private Integer rating;
        private Long doctorId;
        private Long medicineId;
        private String comment;
    }

    public static GetReviewDTO fromEntity(Review review) {
        if (review == null) return null;

        return GetReviewDTO.builder()
                .id(review.getId())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .user(UserDTO.fromEntity(review.getUser()))
                .rating(review.getRating())
                .comment(review.getComment())
                .isPositiveReview(review.isPositiveReview())
                .isNegativeReview(review.isNegativeReview())
                .doctorId(review.getDoctor() != null ? review.getDoctor().getId() : null)
                .medicineId(review.getMedicine() != null ? review.getMedicine().getId() : null)
                .build();
    }

    public static GetReviewDTOWithoutDetails fromEntityWithoutDetails(Review review) {
        if (review == null) return null;

        return GetReviewDTOWithoutDetails.builder()
                .id(review.getId())
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .doctorId(review.getDoctor() != null ? review.getDoctor().getId() : null)
                .medicineId(review.getMedicine() != null ? review.getMedicine().getId() : null)
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}