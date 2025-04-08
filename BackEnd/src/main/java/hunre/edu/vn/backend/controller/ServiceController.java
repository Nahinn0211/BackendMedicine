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

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceDTO.GetServiceDTO> saveOrUpdateService(@RequestPart("service") String serviceJson,@RequestPart(value = "file", required = true) MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServiceDTO.SaveServiceDTO serviceDto = objectMapper.readValue(serviceJson, ServiceDTO.SaveServiceDTO.class);
            System.out.println(serviceDto);
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

    @DeleteMapping("/{id}")
    public String deleteService(@RequestBody List<Long> ids) {
        return serviceService.deleteByList(ids);
    }

    @GetMapping("/by-name")
    public ResponseEntity<List<ServiceDTO.GetServiceDTO>> getServicesByName(@RequestParam String name) {
        List<ServiceDTO.GetServiceDTO> services = serviceService.findByName(name);
        return ResponseEntity.ok(services);
    }
}