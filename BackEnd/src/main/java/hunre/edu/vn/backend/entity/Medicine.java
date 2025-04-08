package hunre.edu.vn.backend.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicines", indexes = {
        @Index(name = "idx_medicine_code", columnList = "code", unique = true),
        @Index(name = "idx_medicine_name", columnList = "name"),
        @Index(name = "idx_medicine_brand", columnList = "brand_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"brand", "attributes", "medicineCategories", "medicineMedias"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Medicine extends BaseEntity {
    @NotBlank(message = "Mã thuốc không được để trống")
    @Pattern(regexp = "^[A-Z0-9]{3,10}$", message = "Mã thuốc phải từ 3-10 ký tự và chỉ gồm chữ hoa và số")
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotBlank(message = "Tên thuốc không được để trống")
    @Size(min = 3, max = 255, message = "Tên thuốc phải từ 3-255 ký tự")
    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String description;

    @Column(name = "usage_instruction", columnDefinition = "nvarchar(MAX)")
    private String usageInstruction;

    @Column(name = "dosage_instruction", columnDefinition = "nvarchar(MAX)")
    private String dosageInstruction;

    @Column(name = "is_prescription_required", nullable = true)
    private Boolean isPrescriptionRequired = false;

    @Formula("(SELECT COALESCE(SUM(a.stock), 0) FROM attributes a WHERE a.medicine_id = id AND a.is_deleted = false)")
    private Integer totalStock;

    @Formula("(SELECT MIN(a.price_out) FROM attributes a WHERE a.medicine_id = id AND a.is_deleted = false)")
    private BigDecimal minPrice;

    @Formula("(SELECT MAX(a.price_out) FROM attributes a WHERE a.medicine_id = id AND a.is_deleted = false)")
    private BigDecimal maxPrice;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<Attribute> attributes = new ArrayList<>();

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<MedicineCategory> medicineCategories = new ArrayList<>();

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<MedicineMedia> medicineMedias = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @ToString.Exclude
    @JoinColumn(name = "brand_id", referencedColumnName = "id", nullable = false)
    private Brand brand;

    @Column(name = "origin", nullable = false, columnDefinition = "nvarchar(MAX)")
    private String origin;

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
        attribute.setMedicine(this);
    }

    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
        attribute.setMedicine(null);
    }

    public void addCategory(Category category) {
        MedicineCategory medicineCategory = new MedicineCategory();
        medicineCategory.setMedicine(this);
        medicineCategory.setCategory(category);
        this.medicineCategories.add(medicineCategory);
    }

    public void removeCategory(Category category) {
        this.medicineCategories.removeIf(medicineCategory ->
                medicineCategory.getCategory().equals(category));
    }

    public void addMedia(String mediaUrl, boolean isMainImage) {
        MedicineMedia media = new MedicineMedia();
        media.setMedicine(this);
        media.setMediaUrl(mediaUrl);
        media.setMainImage(isMainImage);
        this.medicineMedias.add(media);

        // Nếu đây là hình ảnh chính, đảm bảo không có hình ảnh chính nào khác
        if (isMainImage) {
            this.medicineMedias.forEach(m -> {
                if (!m.equals(media)) {
                    m.setMainImage(false);
                }
            });
        }
    }

    public String getMainImageUrl() {
        return this.medicineMedias.stream()
                .filter(MedicineMedia::getMainImage)
                .findFirst()
                .map(MedicineMedia::getMediaUrl)
                .orElse(null);
    }

    public boolean isInStock() {
        return this.totalStock != null && this.totalStock > 0;
    }

}