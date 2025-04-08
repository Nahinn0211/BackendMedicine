package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.PatientProfileDTO;

import java.util.List;
import java.util.Optional;

public interface PatientProfileService {
    List<PatientProfileDTO.GetPatientProfileDTO> findAll();
    Optional<PatientProfileDTO.GetPatientProfileDTO> findById(Long id);
    PatientProfileDTO.GetPatientProfileDTO saveOrUpdate(PatientProfileDTO.SavePatientProfileDTO patientProfileDTO);
    String deleteByList(List<Long> ids);
    Optional<PatientProfileDTO.GetPatientProfileDTO> findByUserId(Long UserId);
    Optional<PatientProfileDTO.GetPatientProfileDTO> updateBalance(Long id, String balance);
    Long getTotalPatients();
}