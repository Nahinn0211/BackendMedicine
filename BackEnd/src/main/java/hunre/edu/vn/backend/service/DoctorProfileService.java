package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.DoctorProfileDTO;

import java.util.List;
import java.util.Optional;

public interface DoctorProfileService {
    List<DoctorProfileDTO.GetDoctorProfileDTO> findAll();
    Optional<DoctorProfileDTO.GetDoctorProfileDTO> findById(Long id);
    DoctorProfileDTO.GetDoctorProfileDTO saveOrUpdate(DoctorProfileDTO.SaveDoctorProfileDTO doctorProfileDTO);
    String deleteByList(List<Long> ids);
    Optional<DoctorProfileDTO.GetDoctorProfileDTO> findDoctorProfileByUserId(Long id);
    Long getTotalDoctors();
}