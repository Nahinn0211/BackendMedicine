package hunre.edu.vn.backend.mapper;

 import hunre.edu.vn.backend.dto.AttributeDTO;
import hunre.edu.vn.backend.dto.CategoryDTO;
import hunre.edu.vn.backend.dto.MedicineDTO;
import hunre.edu.vn.backend.dto.MedicineMediaDTO;
import hunre.edu.vn.backend.entity.Brand;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.Attribute;
import hunre.edu.vn.backend.entity.MedicineCategory;
import hunre.edu.vn.backend.entity.MedicineMedia;
 import hunre.edu.vn.backend.repository.AttributeRepository;
 import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MedicineMapper {

    @Autowired
    protected AttributeRepository attributeRepository;

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "brand", expression = "java(mapBrandSafely(medicine.getBrand()))")
    @Mapping(target = "attributes", ignore = true) // Ignore để set attributes riêng bằng service
    @Mapping(target = "categories", expression = "java(mapCategoriesSafely(medicine))")
    @Mapping(target = "medias", expression = "java(mapMediaSafely(medicine))")
    public abstract MedicineDTO.GetMedicineDTO toGetMedicineDTO(Medicine medicine);

    public MedicineDTO.BrandBasicDTO mapBrandSafely(Brand brand) {
        if (brand == null) return null;
        return MedicineDTO.BrandBasicDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .image(brand.getImage())
                .build();
    }

    // Phương thức này sẽ được gọi bởi AttributeService thay vì ở đây
    public AttributeDTO.GetAttributeDTO mapAttributeToDTO(Attribute attribute) {
        if (attribute == null || Boolean.TRUE.equals(attribute.getIsDeleted())) return null;

        return AttributeDTO.GetAttributeDTO.builder()
                .id(attribute.getId())
                .medicineId(attribute.getMedicine() != null ? attribute.getMedicine().getId() : null)
                .name(attribute.getName())
                .priceIn(attribute.getPriceIn())
                .priceOut(attribute.getPriceOut())
                .expiryDate(attribute.getExpiryDate())
                .stock(attribute.getStock())
                .isNearExpiry(attribute.isNearExpiry())
                .isExpired(attribute.isExpired())
                .createdAt(attribute.getCreatedAt())
                .updatedAt(attribute.getUpdatedAt())
                .build();
    }

    public List<CategoryDTO.GetCategoryDTO> mapCategoriesSafely(Medicine medicine) {
        if (medicine == null || medicine.getMedicineCategories() == null) {
            return Collections.emptyList();
        }

        return medicine.getMedicineCategories().stream()
                .filter(mc -> mc != null && !Boolean.TRUE.equals(mc.getIsDeleted()))
                .map(mc -> mapCategoryFromMedicine(mc))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public CategoryDTO.GetCategoryDTO mapCategoryFromMedicine(MedicineCategory medicineCategory) {
        if (medicineCategory == null || medicineCategory.getCategory() == null ||
                Boolean.TRUE.equals(medicineCategory.getIsDeleted())) {
            return null;
        }

        return CategoryDTO.GetCategoryDTO.builder()
                .id(medicineCategory.getCategory().getId())
                .name(medicineCategory.getCategory().getName())
                 .createdAt(medicineCategory.getCategory().getCreatedAt())
                .updatedAt(medicineCategory.getCategory().getUpdatedAt())
                .build();
    }

    public List<MedicineMediaDTO.GetMedicineMediaDTO> mapMediaSafely(Medicine medicine) {
        if (medicine == null || medicine.getMedicineMedias() == null) {
            return Collections.emptyList();
        }

        return medicine.getMedicineMedias().stream()
                .filter(media -> media != null && !Boolean.TRUE.equals(media.getIsDeleted()))
                .map(this::mapMediaToDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public MedicineMediaDTO.GetMedicineMediaDTO mapMediaToDTO(MedicineMedia medicineMedia) {
        if (medicineMedia == null || Boolean.TRUE.equals(medicineMedia.getIsDeleted())) {
            return null;
        }

        return MedicineMediaDTO.GetMedicineMediaDTO.builder()
                .id(medicineMedia.getId())
                .mediaUrl(medicineMedia.getMediaUrl())
                .mainImage(medicineMedia.getMainImage())
                .medicineId(medicineMedia.getMedicine() != null ? medicineMedia.getMedicine().getId() : null)
                .createdAt(medicineMedia.getCreatedAt())
                .updatedAt(medicineMedia.getUpdatedAt())
                .build();
    }

    public abstract Medicine toMedicineEntity(MedicineDTO.SaveMedicineDTO dto);
}