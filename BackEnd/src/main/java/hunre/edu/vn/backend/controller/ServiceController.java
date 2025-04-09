package hunre.edu.vn.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hunre.edu.vn.backend.dto.ServiceDTO;
import hunre.edu.vn.backend.repository.DoctorServiceRepository;
import hunre.edu.vn.backend.service.ServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServiceService serviceService;
    private final DoctorServiceRepository doctorServiceRepository;

    public ServiceController(ServiceService serviceService, DoctorServiceRepository doctorServiceRepository) {
        this.serviceService = serviceService;
        this.doctorServiceRepository = doctorServiceRepository;
    }

    @GetMapping
    public ResponseEntity<List<ServiceDTO.GetServiceDTO>> getAllServices() {
        List<ServiceDTO.GetServiceDTO> services = serviceService.findAll();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO.GetServiceDTO> getServiceById(@PathVariable Long id) {
        return serviceService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API để lưu hoặc cập nhật dịch vụ và đồng thời liên kết với nhiều bác sĩ
     */
    @PostMapping(value = "/save-with-doctors", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveOrUpdateServiceWithDoctors(
            @RequestPart("serviceWithDoctors") String serviceWithDoctorsJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServiceDTO.ServiceWithDoctorsDTO serviceWithDoctorsDTO = objectMapper.readValue(
                    serviceWithDoctorsJson, ServiceDTO.ServiceWithDoctorsDTO.class);

            Map<String, Object> result = serviceService.saveOrUpdateServiceWithDoctors(serviceWithDoctorsDTO, file);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lưu dịch vụ và liên kết bác sĩ: " + e.getMessage());
        }
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceDTO.GetServiceDTO> saveOrUpdateService(
            @RequestPart("service") String serviceJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServiceDTO.SaveServiceDTO serviceDto = objectMapper.readValue(serviceJson, ServiceDTO.SaveServiceDTO.class);

            if (serviceDto.getId() != null) {
                Optional<ServiceDTO.GetServiceDTO> existingServiceOpt = serviceService.findById(serviceDto.getId());
                if (existingServiceOpt.isPresent()) {
                    ServiceDTO.GetServiceDTO existingService = existingServiceOpt.get();
                    if (file != null && !file.isEmpty()) {
                        serviceService.deleteServiceImage(existingService.getImage());
                        String newImageUrl = serviceService.uploadServiceImage(file);
                        serviceDto.setImage(newImageUrl);
                    } else {
                        serviceDto.setImage(existingService.getImage());
                    }
                    serviceDto.setUpdatedAt(LocalDateTime.now());
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                if (file != null && !file.isEmpty()) {
                    String newImageUrl = serviceService.uploadServiceImage(file);
                    serviceDto.setImage(newImageUrl);
                }
                serviceDto.setCreatedAt(LocalDateTime.now());
                serviceDto.setUpdatedAt(LocalDateTime.now());
            }
            ServiceDTO.GetServiceDTO savedService = serviceService.saveOrUpdate(serviceDto);

            return ResponseEntity.ok(savedService);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping
    public String deleteService(@RequestBody List<Long> ids) {
        return serviceService.deleteByList(ids);
    }

    @GetMapping("/by-name")
    public ResponseEntity<List<ServiceDTO.GetServiceDTO>> getServicesByName(@RequestParam String name) {
        List<ServiceDTO.GetServiceDTO> services = serviceService.findByName(name);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}/doctors")
    public ResponseEntity<List<Long>> getDoctorIdsByServiceId(@PathVariable Long id) {
        List<Long> doctorIds = serviceService.getDoctorIdsByServiceId(id);
        return ResponseEntity.ok(doctorIds);
    }
}