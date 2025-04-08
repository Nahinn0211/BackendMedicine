package hunre.edu.vn.backend.mapper;


import hunre.edu.vn.backend.dto.BrandDTO;
import hunre.edu.vn.backend.dto.MedicineDTO;
import hunre.edu.vn.backend.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    BrandDTO.GetBrandDTO toGetBrandDTO(Brand entity);

    MedicineDTO.BrandBasicDTO toBrandBasicDTO(Brand entity);

    Brand toEntity(BrandDTO.SaveBrandDTO dto);
}
