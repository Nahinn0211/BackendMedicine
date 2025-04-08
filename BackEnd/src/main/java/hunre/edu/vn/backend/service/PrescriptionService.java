package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.PrescriptionDTO;

import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
    List<PrescriptionDTO.GetPrescriptionDTO> findAll();
    Optional<PrescriptionDTO.GetPrescriptionDTO> findById(Long id);
    PrescriptionDTO.GetPrescriptionDTO saveOrUpdate(PrescriptionDTO.SavePrescriptionDTO prescriptionDTO);
    String deleteByList(List<Long> ids);
    List<PrescriptionDTO.GetPrescriptionDTO> findByDoctorId(Long doctorId);
    List<PrescriptionDTO.GetPrescriptionDTO> findByPatientId(Long patientId);
    List<PrescriptionDTO.GetPrescriptionDTO> findByMedicineId(Long medicineId);
}