package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.AppointmentDTO;
import hunre.edu.vn.backend.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {PatientProfileMapper.class, DoctorProfileMapper.class, ServiceBookingMapper.class, ConsultationMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {
    @Mapping(target = "patient", source = "patient")
    @Mapping(target = "doctor", source = "doctor")
    @Mapping(target = "serviceBooking", source = "serviceBooking")
    @Mapping(target = "consultation.appointment", ignore = true)
    AppointmentDTO.GetAppointmentDTO toGetAppointmentDTO(Appointment entity);

    Appointment toEntity(AppointmentDTO.SaveAppointmentDTO dto);
}