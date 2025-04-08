package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.ServiceBookingDTO;
import hunre.edu.vn.backend.entity.ServiceBooking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceBookingMapper {
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ServiceBookingDTO.GetServiceBookingDTO toGetServiceBookingDTO(ServiceBooking entity);

    ServiceBooking toServiceBookingEntity(ServiceBookingDTO.SaveServiceBookingDTO dto);
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "serviceName", source = "service.name")
    @Mapping(target = "patientName", source = "patient.user.fullName")
    @Mapping(target = "doctorName", source = "doctor.user.fullName")
    @Mapping(target = "patientEmail", source = "patient.user.email")
    @Mapping(target = "doctorEmail", source = "doctor.user.email")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ServiceBookingDTO.DetailedServiceBookingDto toDetailedServiceBookingDto(ServiceBooking entity);
}