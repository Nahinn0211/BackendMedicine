package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.DoctorServiceDTO;
import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.DoctorService;
import hunre.edu.vn.backend.entity.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DoctorServiceMapper {

     @Mapping(source = "doctor.id", target = "doctorId")
     @Mapping(source = "service.id", target = "serviceId")
     DoctorServiceDTO.GetDoctorServiceDTO toGetDoctorServiceDTO(DoctorService entity);

     @Mapping(source = "serviceId", target = "service", qualifiedByName = "idToService")
     @Mapping(source = "doctorId", target = "doctor", qualifiedByName = "idToDoctor")
     DoctorService toEntity(DoctorServiceDTO.SaveDoctorServiceDTO dto);

     @Named("idToService")
     default Service idToService(Long id) {
          if (id == null) {
               return null;
          }
          Service service = new Service();
          service.setId(id);
          return service;
     }

     @Named("idToDoctor")
     default DoctorProfile idToDoctor(Long id) {
          if (id == null) {
               return null;
          }
          DoctorProfile doctorProfile = new DoctorProfile();
          doctorProfile.setId(id);
          return doctorProfile;
     }
}