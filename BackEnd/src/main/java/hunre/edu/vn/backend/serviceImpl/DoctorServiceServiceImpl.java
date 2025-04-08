package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.DoctorServiceDTO;
import hunre.edu.vn.backend.entity.DoctorService;
import hunre.edu.vn.backend.mapper.DoctorServiceMapper;
import hunre.edu.vn.backend.repository.DoctorServiceRepository;
import hunre.edu.vn.backend.service.DoctorServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorServiceServiceImpl implements DoctorServiceService {

    private final DoctorServiceRepository doctorServiceRepository;
    private final DoctorServiceMapper doctorServiceMapper;

    @Autowired
    public DoctorServiceServiceImpl(DoctorServiceRepository doctorServiceRepository, DoctorServiceMapper doctorServiceMapper) {
        this.doctorServiceRepository = doctorServiceRepository;
        this.doctorServiceMapper = doctorServiceMapper;
    }
    @Override
    public List<DoctorServiceDTO.GetDoctorServiceDTO> findByServiceId(Long serviceId) {
        List<DoctorService> doctorServices = doctorServiceRepository.findByService_Id(serviceId);
        List<DoctorServiceDTO.GetDoctorServiceDTO> doctorServiceDtos = new ArrayList<>();

        for (DoctorService doctorService : doctorServices) {
            DoctorServiceDTO.GetDoctorServiceDTO doctorServiceDto = new DoctorServiceDTO.GetDoctorServiceDTO();
            doctorServiceDto.setId(doctorService.getId());
            doctorServiceDto.setServiceId(doctorService.getService().getId());
            doctorServiceDto.setDoctorId(doctorService.getDoctor().getId());
            doctorServiceDtos.add(doctorServiceDto);
        }

        return doctorServiceDtos;
    }
    @Override
    public List<DoctorServiceDTO.GetDoctorServiceDTO> findAll() {
        return doctorServiceRepository.findAll().stream()
                .map(doctorServiceMapper::toGetDoctorServiceDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DoctorServiceDTO.GetDoctorServiceDTO> findById(Long id) {
        return doctorServiceRepository.findById(id)
                .map(doctorServiceMapper::toGetDoctorServiceDTO);
    }

    @Override
    public DoctorServiceDTO.GetDoctorServiceDTO save(DoctorServiceDTO.SaveDoctorServiceDTO doctorServiceDTO) {
        DoctorService doctorService = doctorServiceMapper.toEntity(doctorServiceDTO);
        DoctorService savedDoctorService = doctorServiceRepository.save(doctorService);
        return doctorServiceMapper.toGetDoctorServiceDTO(savedDoctorService);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (doctorServiceRepository.existsById(id)) {
                doctorServiceRepository.softDelete(id);
            }
        }

        return "Đã xóa thành công " + ids.size() +" liên kết bác sĩ với dịch vụ.";
    }

    @Override
    public List<DoctorServiceDTO.GetDoctorServiceDTO> findByDoctorId(Long doctorId) {
        return doctorServiceRepository.findByDoctor_Id(doctorId).stream()
                .map(doctorServiceMapper::toGetDoctorServiceDTO)
                .collect(Collectors.toList());
    }
}