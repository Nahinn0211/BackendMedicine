package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.PatientProfileDTO;
import hunre.edu.vn.backend.entity.PatientProfile;
import hunre.edu.vn.backend.entity.User;
import hunre.edu.vn.backend.mapper.PatientProfileMapper;
import hunre.edu.vn.backend.repository.PatientProfileRepository;
import hunre.edu.vn.backend.repository.UserRepository;
import hunre.edu.vn.backend.service.PatientProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientProfileServiceImpl implements PatientProfileService {
    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;
    private final PatientProfileMapper patientProfileMapper;

    public PatientProfileServiceImpl(
            PatientProfileRepository patientProfileRepository,
            UserRepository userRepository,
            PatientProfileMapper patientProfileMapper) {
        this.patientProfileRepository = patientProfileRepository;
        this.userRepository = userRepository;
        this.patientProfileMapper = patientProfileMapper;
    }

    @Override
    public List<PatientProfileDTO.GetPatientProfileDTO> findAll() {
        return patientProfileRepository.findAllActive()
                .stream()
                .map(patientProfileMapper::toGetPatientProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PatientProfileDTO.GetPatientProfileDTO> findById(Long id) {
        return patientProfileRepository.findActiveById(id)
                .map(patientProfileMapper::toGetPatientProfileDTO);
    }

    @Override
    public Optional<PatientProfileDTO.GetPatientProfileDTO> findByUserId(Long UserId) {
        return patientProfileRepository.findByUserId(UserId)
                .map(patientProfileMapper::toGetPatientProfileDTO);
    }

    @Override
    @Transactional
    public PatientProfileDTO.GetPatientProfileDTO saveOrUpdate(PatientProfileDTO.SavePatientProfileDTO patientProfileDTO) {
        PatientProfile patientProfile;

        if (patientProfileDTO.getId() == null || patientProfileDTO.getId() == 0) {
            // INSERT case
            patientProfile = new PatientProfile();
            patientProfile.setCreatedAt(LocalDateTime.now());
            patientProfile.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<PatientProfile> existingProfile = patientProfileRepository.findById(patientProfileDTO.getId());
            if (existingProfile.isEmpty()) {
                throw new RuntimeException("Patient profile not found with ID: " + patientProfileDTO.getId());
            }
            patientProfile = existingProfile.get();
            patientProfile.setUpdatedAt(LocalDateTime.now());
        }

        // Xử lý user relationship
        User user = userRepository.findById(patientProfileDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + patientProfileDTO.getUserId()));
        patientProfile.setUser(user);

        // Cập nhật các trường khác
        patientProfile.setBloodType(patientProfileDTO.getBloodType());
        patientProfile.setMedicalHistory(patientProfileDTO.getMedicalHistory());
        patientProfile.setAllergies(patientProfileDTO.getAllergies());
        patientProfile.setAccountBalance(patientProfileDTO.getAccountBalance());

        PatientProfile savedProfile = patientProfileRepository.save(patientProfile);
        return patientProfileMapper.toGetPatientProfileDTO(savedProfile);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (patientProfileRepository.existsById(id)) {
                patientProfileRepository.softDelete(id);
            }
        }

        return "Đã xóa " + ids.size() + " bệnh nhân";
    }

    @Override
    public Optional<PatientProfileDTO.GetPatientProfileDTO> updateBalance(Long id, String balance) {
        Optional<PatientProfile> existingProfile = patientProfileRepository.findActiveById(id);

        if (existingProfile.isPresent()) {
            try {
                // Loại bỏ bất kỳ ký tự không phải số nào (trừ dấu chấm)
                String cleanedBalance = balance.replaceAll("[^0-9.]", "");
                BigDecimal newBalance = new BigDecimal(cleanedBalance);

                PatientProfile patientProfile = existingProfile.get();
                patientProfile.setAccountBalance(newBalance);

                PatientProfile updatedPatientProfile = patientProfileRepository.save(patientProfile);
                return Optional.of(patientProfileMapper.toGetPatientProfileDTO(updatedPatientProfile));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid balance format: " + balance, e);
            }
        } else {
            return Optional.empty();
        }
    }
    @Override
    public Long getTotalPatients() {
        return patientProfileRepository.count();
    }
}