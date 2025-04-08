package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.ConsultationDTO;
import hunre.edu.vn.backend.entity.Consultation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsultationMapper {
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    ConsultationDTO.GetConsultationDTO toGetConsultationDTO(Consultation entity);

    Consultation toEntity(ConsultationDTO.SaveConsultationDTO dto);
}