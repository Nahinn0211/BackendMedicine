package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.PrescriptionDTO;
import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.PatientProfile;
import hunre.edu.vn.backend.entity.Prescription;
import hunre.edu.vn.backend.mapper.PrescriptionMapper;
import hunre.edu.vn.backend.repository.DoctorProfileRepository;
import hunre.edu.vn.backend.repository.MedicineRepository;
import hunre.edu.vn.backend.repository.PatientProfileRepository;
import hunre.edu.vn.backend.repository.PrescriptionRepository;
import hunre.edu.vn.backend.service.PrescriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoctorProfileRepository doctorRepository;
    private final PatientProfileRepository patientRepository;
    private final MedicineRepository medicineRepository;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionServiceImpl(
            PrescriptionRepository prescriptionRepository,
            DoctorProfileRepository doctorRepository,
            PatientProfileRepository patientRepository,
            MedicineRepository medicineRepository,
            PrescriptionMapper prescriptionMapper) {
        this.prescriptionRepository = prescriptionRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.medicineRepository = medicineRepository;
        this.prescriptionMapper = prescriptionMapper;
    }

    @Override
    public List<PrescriptionDTO.GetPrescriptionDTO> findAll() {
        return prescriptionRepository.findAllActive()
                .stream()
                .map(prescriptionMapper::toGetPrescriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PrescriptionDTO.GetPrescriptionDTO> findById(Long id) {
        return prescriptionRepository.findActiveById(id)
                .map(prescriptionMapper::toGetPrescriptionDTO);
    }

    @Override
    @Transactional
    public PrescriptionDTO.GetPrescriptionDTO saveOrUpdate(PrescriptionDTO.SavePrescriptionDTO prescriptionDTO) {
        Prescription prescription;

        if (prescriptionDTO.getId() == null || prescriptionDTO.getId() == 0) {
            // INSERT case
            prescription = new Prescription();
            prescription.setCreatedAt(LocalDateTime.now());
            prescription.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Prescription> existingPrescription = prescriptionRepository.findActiveById(prescriptionDTO.getId());
            if (existingPrescription.isEmpty()) {
                throw new RuntimeException("Prescription not found with ID: " + prescriptionDTO.getId());
            }
            prescription = existingPrescription.get();
            prescription.setUpdatedAt(LocalDateTime.now());
        }

        // SỬA: Thay đổi kiểu biến doctor từ Repository thành Entity
        DoctorProfile doctor = doctorRepository.findActiveById(prescriptionDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + prescriptionDTO.getDoctorId()));
        prescription.setDoctor(doctor);

        // SỬA: Thay đổi kiểu biến patient từ Repository thành Entity
        PatientProfile patient = patientRepository.findActiveById(prescriptionDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + prescriptionDTO.getPatientId()));
        prescription.setPatient(patient);

        // Xử lý medicine relationship
        Medicine medicine = medicineRepository.findActiveById(prescriptionDTO.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + prescriptionDTO.getMedicineId()));
        prescription.setMedicine(medicine);

        // Cập nhật trường còn lại
        prescription.setDosage(prescriptionDTO.getDosage());

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return prescriptionMapper.toGetPrescriptionDTO(savedPrescription);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (prescriptionRepository.existsById(id)){
                prescriptionRepository.softDelete(id);
            }
        }

        return "Đã xóa " + ids.size() + " đơn thuốc";
    }

    @Override
    public List<PrescriptionDTO.GetPrescriptionDTO> findByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctor_Id(doctorId)
                .stream()
                .map(prescriptionMapper::toGetPrescriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO.GetPrescriptionDTO> findByPatientId(Long patientId) {
        return prescriptionRepository.findByPatient_Id(patientId)
                .stream()
                .map(prescriptionMapper::toGetPrescriptionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionDTO.GetPrescriptionDTO> findByMedicineId(Long medicineId) {
        return prescriptionRepository.findByMedicine_Id(medicineId)
                .stream()
                .map(prescriptionMapper::toGetPrescriptionDTO)
                .collect(Collectors.toList());
    }
}