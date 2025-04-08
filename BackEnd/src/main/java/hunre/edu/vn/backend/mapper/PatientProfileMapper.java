package hunre.edu.vn.backend.mapper;


import hunre.edu.vn.backend.dto.PatientProfileDTO;
import hunre.edu.vn.backend.entity.PatientProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientProfileMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PatientProfileDTO.GetPatientProfileDTO toGetPatientProfileDTO(PatientProfile entity);

    PatientProfile toPatientProfileEntity(PatientProfileDTO.SavePatientProfileDTO dto);
}