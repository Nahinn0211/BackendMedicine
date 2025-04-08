package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "medicine_medias", indexes = {
        @Index(name = "idx_medicine_media_medicine", columnList = "medicine_id"),
        @Index(name = "idx_medicine_media_main", columnList = "main_image")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MedicineMedia extends BaseEntity {
    @NotNull(message = "Thuốc không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @NotBlank(message = "URL hình ảnh không được để trống")
    @Size(max = 500, message = "URL hình ảnh không được vượt quá 500 ký tự")
    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @NotNull(message = "Trạng thái hình ảnh chính không được trống")
    @Column(name = "main_image", nullable = false)
    private Boolean mainImage = false;

    public void setAsMainImage() {
        // Đảm bảo chỉ có một ảnh chính
        if (this.medicine != null) {
            this.medicine.getMedicineMedias().forEach(media -> {
                if (!media.equals(this)) {
                    media.setMainImage(false);
                }
            });
        }
        this.mainImage = true;
    }
}