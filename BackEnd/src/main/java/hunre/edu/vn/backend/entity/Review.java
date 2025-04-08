package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_user", columnList = "user_id"),
        @Index(name = "idx_review_doctor", columnList = "doctor_id"),
        @Index(name = "idx_review_medicine", columnList = "medicine_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Review extends BaseEntity {
    @NotNull(message = "Người dùng không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Đánh giá không được trống")
    @Min(value = 1, message = "Đánh giá tối thiểu là 1")
    @Max(value = 5, message = "Đánh giá tối đa là 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Size(max = 1000, message = "Bình luận không được vượt quá 1000 ký tự")
    @Column(name = "comment", columnDefinition = "nvarchar(MAX)")
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "doctor_id", nullable = true)
    private DoctorProfile doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "medicine_id", nullable = true)
    private Medicine medicine;

    public boolean isValid() {
        return this.rating != null &&
                this.rating >= 1 &&
                this.rating <= 5;
    }

    public void updateComment(String newComment) {
        if (newComment != null && newComment.length() <= 1000) {
            this.comment = newComment;
        }
    }

    public boolean isPositiveReview() {
        return this.rating != null && this.rating >= 4;
    }

    public boolean isNegativeReview() {
        return this.rating != null && this.rating <= 2;
    }
}