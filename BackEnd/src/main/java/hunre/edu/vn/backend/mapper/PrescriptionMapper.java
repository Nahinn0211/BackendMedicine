package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.PrescriptionDTO;
import hunre.edu.vn.backend.entity.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PrescriptionMapper {
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "medicineId", source = "medicine.id")
    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    PrescriptionDTO.GetPrescriptionDTO toGetPrescriptionDTO(Prescription entity);

    Prescription toPrescriptionEntity(PrescriptionDTO.SavePrescriptionDTO dto);
}