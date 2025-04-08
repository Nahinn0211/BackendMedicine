package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.SocialAccountDTO;
import hunre.edu.vn.backend.entity.SocialAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SocialAccountMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    SocialAccountDTO.GetSocialAccountDTO toGetSocialAccountDTO(SocialAccount entity);

    SocialAccount toSocialAccountEntity(SocialAccountDTO.SaveSocialAccountDTO dto);
}