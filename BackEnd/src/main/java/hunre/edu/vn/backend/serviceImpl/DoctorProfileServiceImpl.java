package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.DoctorProfileDTO;
import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.User;
import hunre.edu.vn.backend.mapper.DoctorProfileMapper;
import hunre.edu.vn.backend.repository.DoctorProfileRepository;
import hunre.edu.vn.backend.repository.UserRepository;
import hunre.edu.vn.backend.service.DoctorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {

    private final DoctorProfileRepository doctorProfileRepository;
    private final UserRepository userRepository;
    private final DoctorProfileMapper doctorProfileMapper;

    @Autowired
    public DoctorProfileServiceImpl(
            DoctorProfileRepository doctorProfileRepository,
            UserRepository userRepository,
            DoctorProfileMapper doctorProfileMapper) {
        this.doctorProfileRepository = doctorProfileRepository;
        this.userRepository = userRepository;
        this.doctorProfileMapper = doctorProfileMapper;
    }

    @Override
    public List<DoctorProfileDTO.GetDoctorProfileDTO> findAll() {
        return doctorProfileRepository.findAllActive().stream()
                .map(doctorProfileMapper::toGetDoctorProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DoctorProfileDTO.GetDoctorProfileDTO> findById(Long id) {
        return doctorProfileRepository.findActiveById(id)
                .map(doctorProfileMapper::toGetDoctorProfileDTO);
    }

    @Override
    @Transactional
    public DoctorProfileDTO.GetDoctorProfileDTO saveOrUpdate(DoctorProfileDTO.SaveDoctorProfileDTO doctorProfileDTO) {
        DoctorProfile doctorProfile;

        if (doctorProfileDTO.getId() == null || doctorProfileDTO.getId() == 0) {
            // INSERT case
            doctorProfile = new DoctorProfile();
            doctorProfile.setCreatedAt(LocalDateTime.now());
            doctorProfile.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<DoctorProfile> existingDoctorProfile = doctorProfileRepository.findActiveById(doctorProfileDTO.getId());
            if (existingDoctorProfile.isEmpty()) {
                throw new RuntimeException("Doctor profile not found with ID: " + doctorProfileDTO.getId());
            }
            doctorProfile = existingDoctorProfile.get();
            doctorProfile.setUpdatedAt(LocalDateTime.now());
        }

        User user = userRepository.findActiveById(doctorProfileDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + doctorProfileDTO.getUserId()));
        doctorProfile.setUser(user);

        doctorProfile.setExperience(doctorProfileDTO.getExperience());
        doctorProfile.setSpecialization(doctorProfileDTO.getSpecialization());
        doctorProfile.setWorkplace(doctorProfileDTO.getWorkplace());
        doctorProfile.setAccountBalance(doctorProfileDTO.getAccountBalance());

        DoctorProfile savedDoctorProfile = doctorProfileRepository.save(doctorProfile);
        return doctorProfileMapper.toGetDoctorProfileDTO(savedDoctorProfile);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id: ids){
            if (doctorProfileRepository.existsById(id)){
                doctorProfileRepository.softDelete(id);
            }
        }

        return "Đã xóa thành công "+ ids.size() + " bác sĩ";
    }

    @Override
    public Optional<DoctorProfileDTO.GetDoctorProfileDTO> findDoctorProfileByUserId(Long id) {
        return doctorProfileRepository.findByUser_Id(id)
                .map(doctorProfileMapper::toGetDoctorProfileDTO);
    }

    @Override
    public Long getTotalDoctors() {
        return doctorProfileRepository.count();
    }
}