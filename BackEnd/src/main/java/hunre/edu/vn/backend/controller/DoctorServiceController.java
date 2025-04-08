package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.DoctorProfileDTO;
import hunre.edu.vn.backend.dto.DoctorServiceDTO;
import hunre.edu.vn.backend.mapper.DoctorProfileMapper;
import hunre.edu.vn.backend.repository.DoctorProfileRepository;
import hunre.edu.vn.backend.repository.DoctorServiceRepository;
import hunre.edu.vn.backend.service.DoctorServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor-services")
public class DoctorServiceController {

    private final DoctorServiceService doctorServiceService;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorProfileMapper doctorProfileMapper;
    private final DoctorServiceRepository doctorServiceRepository;

    @Autowired
    public DoctorServiceController(DoctorServiceService doctorServiceService, DoctorProfileRepository doctorProfileRepository, DoctorProfileMapper doctorProfileMapper, DoctorServiceRepository doctorServiceRepository) {
        this.doctorServiceService = doctorServiceService;
        this.doctorProfileRepository = doctorProfileRepository;
        this.doctorProfileMapper = doctorProfileMapper;
        this.doctorServiceRepository = doctorServiceRepository;
    }

    @GetMapping
    public ResponseEntity<List<DoctorServiceDTO.GetDoctorServiceDTO>> getAllDoctorServices() {
        List<DoctorServiceDTO.GetDoctorServiceDTO> services = doctorServiceService.findAll();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorServiceDTO.GetDoctorServiceDTO> getDoctorServiceById(@PathVariable Long id) {
        return doctorServiceService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<DoctorProfileDTO.GetDoctorProfileDTO>> getDoctorServicesByServiceId(@PathVariable Long serviceId) {
        return ResponseEntity.ok(
                doctorServiceService.findByServiceId(serviceId).stream()
                        .map(service ->
                                doctorProfileRepository.findById(service.getDoctorId())
                                        .map(doctorProfileMapper::toGetDoctorProfileDTO)
                                        .orElse(null)
                        )
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorServiceDTO.GetDoctorServiceDTO>> getDoctorServicesByDoctorId(@PathVariable Long doctorId) {
        List<DoctorServiceDTO.GetDoctorServiceDTO> services = doctorServiceService.findByDoctorId(doctorId);
        return ResponseEntity.ok(services);
    }

    @PostMapping
    public ResponseEntity<DoctorServiceDTO.GetDoctorServiceDTO> createDoctorService(@RequestBody DoctorServiceDTO.SaveDoctorServiceDTO doctorServiceDTO) {
        DoctorServiceDTO.GetDoctorServiceDTO savedService = doctorServiceService.save(doctorServiceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorServiceDTO.GetDoctorServiceDTO> updateDoctorService(
            @PathVariable Long id,
            @RequestBody DoctorServiceDTO.SaveDoctorServiceDTO doctorServiceDTO) {

        // Kiểm tra xem dịch vụ có tồn tại không
        if (!doctorServiceService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        // Đảm bảo ID trong DTO khớp với ID trong đường dẫn
        doctorServiceDTO.setId(id);

        DoctorServiceDTO.GetDoctorServiceDTO updatedService = doctorServiceService.save(doctorServiceDTO);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/{id}")
    public String deleteDoctorService(@RequestBody List<Long> ids) {
        return doctorServiceService.deleteByList(ids);
    }
}