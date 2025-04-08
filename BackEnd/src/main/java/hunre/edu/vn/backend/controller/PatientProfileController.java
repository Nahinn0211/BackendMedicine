package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.PatientProfileDTO;
import hunre.edu.vn.backend.service.PatientProfileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient-profiles")
@RequiredArgsConstructor
public class PatientProfileController {

    private final PatientProfileService patientProfileService;

    @GetMapping
    @Operation(summary = "Get all patient profiles")
    public ResponseEntity<List<PatientProfileDTO.GetPatientProfileDTO>> getAllProfiles() {
        List<PatientProfileDTO.GetPatientProfileDTO> profiles = patientProfileService.findAll();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/total")
    @Operation(summary = "Get total number of patients")
    public ResponseEntity<Long> getTotalPatients() {
        Long totalPatients = patientProfileService.getTotalPatients();
        return ResponseEntity.ok(totalPatients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient profile by ID")
    public ResponseEntity<PatientProfileDTO.GetPatientProfileDTO> getProfileById(@PathVariable Long id) {
        return patientProfileService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-user/{id}")
    public ResponseEntity<PatientProfileDTO.GetPatientProfileDTO> getProfileByUserId(@PathVariable Long id) {
        return patientProfileService.findByUserId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    @Operation(summary = "Create or update patient profile")
    public ResponseEntity<PatientProfileDTO.GetPatientProfileDTO> saveOrUpdateProfile(
            @RequestBody PatientProfileDTO.SavePatientProfileDTO request) {
        PatientProfileDTO.GetPatientProfileDTO savedProfile = patientProfileService.saveOrUpdate(request);
        return ResponseEntity.ok(savedProfile);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete patient profile")
    public String deleteProfile(@RequestBody List<Long> ids) {
        return patientProfileService.deleteByList(ids);
    }

    @PutMapping("/update/balance/{id}")
    public ResponseEntity<PatientProfileDTO.GetPatientProfileDTO> updateBalance(@RequestBody String balance, @PathVariable Long id) {
        if (!patientProfileService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }else{
            return patientProfileService.updateBalance(id, balance)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
    }
}