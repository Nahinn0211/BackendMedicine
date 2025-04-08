package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.UserRoleDTO;
import hunre.edu.vn.backend.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserRoleDTO.GetUserRoleDTO toGetUserRoleDTO(UserRole entity);

    UserRole toUserRoleEntity(UserRoleDTO.SaveUserRoleDTO dto);
}