package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.UserDTO;
import hunre.edu.vn.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserDTO.GetUserDTO toGetUserDTO(User entity);

    User totoGetUserDtoEntity(UserDTO.SaveUserDTO dto);
}