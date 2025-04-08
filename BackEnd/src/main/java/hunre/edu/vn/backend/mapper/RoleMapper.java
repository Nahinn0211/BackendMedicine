package hunre.edu.vn.backend.mapper;


import hunre.edu.vn.backend.dto.RoleDTO;
import hunre.edu.vn.backend.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    RoleDTO.GetRoleDTO toGetRoleDTO(Role entity);

    Role toRoleEntity(RoleDTO.SaveRoleDTO dto);
}