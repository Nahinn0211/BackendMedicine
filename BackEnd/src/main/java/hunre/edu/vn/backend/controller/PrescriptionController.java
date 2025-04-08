package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.PrescriptionDTO;
import hunre.edu.vn.backend.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionDTO.GetPrescriptionDTO>> getAllPrescriptions() {
        List<PrescriptionDTO.GetPrescriptionDTO> prescriptions = prescriptionService.findAll();
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO.GetPrescriptionDTO> getPrescriptionById(@PathVariable Long id) {
        return prescriptionService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<PrescriptionDTO.GetPrescriptionDTO> saveOrUpdatePrescription(@RequestBody PrescriptionDTO.SavePrescriptionDTO prescriptionDTO) {
        PrescriptionDTO.GetPrescriptionDTO savedPrescription = prescriptionService.saveOrUpdate(prescriptionDTO);
        return ResponseEntity.ok(savedPrescription);
    }

    @DeleteMapping("/{id}")
    public String deletePrescription(@RequestBody List<Long> ids) {
        return prescriptionService.deleteByList(ids);
    }

    @GetMapping("/by-doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionDTO.GetPrescriptionDTO>> getPrescriptionsByDoctorId(@PathVariable Long doctorId) {
        List<PrescriptionDTO.GetPrescriptionDTO> prescriptions = prescriptionService.findByDoctorId(doctorId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<PrescriptionDTO.GetPrescriptionDTO>> getPrescriptionsByPatientId(@PathVariable Long patientId) {
        List<PrescriptionDTO.GetPrescriptionDTO> prescriptions = prescriptionService.findByPatientId(patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/by-medicine/{medicineId}")
    public ResponseEntity<List<PrescriptionDTO.GetPrescriptionDTO>> getPrescriptionsByMedicineId(@PathVariable Long medicineId) {
        List<PrescriptionDTO.GetPrescriptionDTO> prescriptions = prescriptionService.findByMedicineId(medicineId);
        return ResponseEntity.ok(prescriptions);
    }
}