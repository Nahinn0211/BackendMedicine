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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MedicineMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "brand", expression = "java(mapBrandSafely(medicine.getBrand()))")
    @Mapping(target = "attributes", expression = "java(mapAttributesSafely(medicine))")
    @Mapping(target = "categories", expression = "java(mapCategoriesSafely(medicine))")
    @Mapping(target = "medias", expression = "java(mapMediaSafely(medicine))")
    MedicineDTO.GetMedicineDTO toGetMedicineDTO(Medicine medicine);

    default MedicineDTO.BrandBasicDTO mapBrandSafely(Brand brand) {
        if (brand == null) return null;
        return MedicineDTO.BrandBasicDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .build();
    }

    default List<AttributeDTO.GetAttributeDTO> mapAttributesSafely(Medicine medicine) {
        return Optional.ofNullable(medicine.getAttributes())
                .map(attrs -> attrs.stream()
                        .filter(attr -> attr != null && Boolean.FALSE.equals(attr.getIsDeleted()))
                        .map(this::mapAttributeSafely)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    default AttributeDTO.GetAttributeDTO mapAttributeSafely(Attribute attribute) {
        if (attribute == null || Boolean.TRUE.equals(attribute.getIsDeleted())) return null;

        return AttributeDTO.GetAttributeDTO.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .priceIn(attribute.getPriceIn())
                .priceOut(attribute.getPriceOut())
                .expiryDate(attribute.getExpiryDate())
                .stock(attribute.getStock())
                .isNearExpiry(attribute.isNearExpiry())
                .build();
    }

    default List<CategoryDTO.GetCategoryDTO> mapCategoriesSafely(Medicine medicine) {
        return Optional.ofNullable(medicine.getMedicineCategories())
                .map(categories -> categories.stream()
                        .map(this::mapCategorySafely)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    default CategoryDTO.GetCategoryDTO mapCategorySafely(MedicineCategory medicineCategory) {
        if (medicineCategory == null || Boolean.TRUE.equals(medicineCategory.getIsDeleted())) return null;

        return CategoryDTO.GetCategoryDTO.builder()
                .id(medicineCategory.getCategory().getId())
                .name(medicineCategory.getCategory().getName())
                .build();
    }

    default List<MedicineMediaDTO.GetMedicineMediaDTO> mapMediaSafely(Medicine medicine) {
        return Optional.ofNullable(medicine.getMedicineMedias())
                .map(medias -> medias.stream()
                        .map(this::mapMediaSafely)
                        .collect(Collectors.toList()))
                .orElse(null);
    }

    default MedicineMediaDTO.GetMedicineMediaDTO mapMediaSafely(MedicineMedia medicineMedia) {
        if (medicineMedia == null || Boolean.TRUE.equals(medicineMedia.getIsDeleted())) return null;

        return MedicineMediaDTO.GetMedicineMediaDTO.builder()
                .id(medicineMedia.getId())
                .mediaUrl(medicineMedia.getMediaUrl())
                .mainImage(medicineMedia.getMainImage())
                .build();
    }

    Medicine toMedicineEntity(MedicineDTO.SaveMedicineDTO dto);
}