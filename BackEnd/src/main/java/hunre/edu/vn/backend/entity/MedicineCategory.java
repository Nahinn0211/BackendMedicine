package hunre.edu.vn.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "medicine_categories", indexes = {
        @Index(name = "idx_medicine_category_unique", columnList = "medicine_id,category_id", unique = true),
        @Index(name = "idx_medicine_category_medicine", columnList = "medicine_id"),
        @Index(name = "idx_medicine_category_category", columnList = "category_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MedicineCategory extends BaseEntity {
    @NotNull(message = "Thuốc không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @NotNull(message = "Danh mục không được trống")
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}