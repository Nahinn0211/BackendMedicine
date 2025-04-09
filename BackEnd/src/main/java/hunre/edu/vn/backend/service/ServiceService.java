package hunre.edu.vn.backend.service;

 import hunre.edu.vn.backend.dto.ServiceDTO;
 import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
 import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceService {
    List<ServiceDTO.GetServiceDTO> findAll();
    Optional<ServiceDTO.GetServiceDTO> findById(Long id);
    ServiceDTO.GetServiceDTO saveOrUpdate(ServiceDTO.SaveServiceDTO serviceDTO);
    String deleteByList(List<Long> ids);
    String uploadServiceImage(MultipartFile file) throws IOException;
    String deleteServiceImage(String image);
    List<ServiceDTO.GetServiceDTO> findByName(String name);


    Map<String, Object> saveOrUpdateServiceWithDoctors(ServiceDTO.ServiceWithDoctorsDTO serviceWithDoctorsDTO, MultipartFile file) throws Exception;


    List<Long> getDoctorIdsByServiceId(Long serviceId);
}