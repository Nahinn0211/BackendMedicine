package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.AttributeDTO;
import hunre.edu.vn.backend.entity.Attribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttributeMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "medicineId", source = "medicine.id")
    AttributeDTO.GetAttributeDTO toGetAttributeDTO(Attribute entity);

    Attribute toEntity(AttributeDTO.SaveAttributeDTO dto);
}