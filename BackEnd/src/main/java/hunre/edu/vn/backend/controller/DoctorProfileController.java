package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.DoctorProfileDTO;
import hunre.edu.vn.backend.service.DoctorProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor-profiles")
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    public DoctorProfileController(DoctorProfileService doctorProfileService) {
        this.doctorProfileService = doctorProfileService;
    }

    @GetMapping
    public ResponseEntity<List<DoctorProfileDTO.GetDoctorProfileDTO>> getAllDoctorProfiles() {
        List<DoctorProfileDTO.GetDoctorProfileDTO> doctorProfiles = doctorProfileService.findAll();
        return ResponseEntity.ok(doctorProfiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorProfileDTO.GetDoctorProfileDTO> getDoctorProfileById(@PathVariable Long id) {
        return doctorProfileService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<DoctorProfileDTO.GetDoctorProfileDTO> saveOrUpdateDoctorProfile(@RequestBody DoctorProfileDTO.SaveDoctorProfileDTO doctorProfileDTO) {
        DoctorProfileDTO.GetDoctorProfileDTO savedDoctorProfile = doctorProfileService.saveOrUpdate(doctorProfileDTO);
        return ResponseEntity.ok(savedDoctorProfile);
    }

    @DeleteMapping("/{id}")
    public String deleteDoctorProfile(@RequestBody List<Long> ids) {
       return doctorProfileService.deleteByList(ids);
    }

    @GetMapping("/by-user/{id}")
    public Optional<DoctorProfileDTO.GetDoctorProfileDTO> getDoctorProfileByUserId(@PathVariable Long id) {
        return doctorProfileService.findDoctorProfileByUserId(id);
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalDoctors() {
        Long totalDoctors = doctorProfileService.getTotalDoctors();
        return ResponseEntity.ok(totalDoctors);
    }
}