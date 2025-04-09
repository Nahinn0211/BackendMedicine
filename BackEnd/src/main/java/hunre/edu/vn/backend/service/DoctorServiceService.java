package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.DoctorServiceDTO;
import hunre.edu.vn.backend.entity.DoctorService;

import java.util.List;
import java.util.Optional;

public interface DoctorServiceService {
    List<DoctorServiceDTO.GetDoctorServiceDTO> findAll();
    Optional<DoctorServiceDTO.GetDoctorServiceDTO> findById(Long id);
    DoctorServiceDTO.GetDoctorServiceDTO save(DoctorServiceDTO.SaveDoctorServiceDTO doctorServiceDTO);
    String deleteByList(List<Long> ids);
    List<DoctorServiceDTO.GetDoctorServiceDTO> findByDoctorId(Long doctorId);
    List<DoctorServiceDTO.GetDoctorServiceDTO> findByServiceId(Long serviceId);

    // Chuyển đổi từ entity sang DTO
    DoctorServiceDTO.GetDoctorServiceDTO convertToDTO(DoctorService doctorService);
}