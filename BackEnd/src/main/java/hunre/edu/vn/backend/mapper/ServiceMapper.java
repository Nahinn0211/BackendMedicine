package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.DoctorProfileDTO;
import hunre.edu.vn.backend.dto.ServiceDTO;
import hunre.edu.vn.backend.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "doctorProfile", expression = "java(getDoctorProfile(entity))")
    ServiceDTO.GetServiceDTO toGetServiceDTO(Service entity);

    Service toServiceEntity(ServiceDTO.SaveServiceDTO dto);

    default DoctorProfileDTO.GetDoctorProfileDTO getDoctorProfile(Service service) {
        if (service.getDoctorServices() != null && !service.getDoctorServices().isEmpty()) {
            return DoctorProfileDTO.fromEntity(service.getDoctorServices().get(0).getDoctor());
        }
        return null;
    }
}