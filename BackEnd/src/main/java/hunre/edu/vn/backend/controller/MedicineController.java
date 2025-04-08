package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.annotation.RequireAuthentication;
import hunre.edu.vn.backend.dto.MedicineDTO;
import hunre.edu.vn.backend.service.MedicineService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.ion.Decimal;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineDTO.GetMedicineDTO> getMedicineById(@PathVariable Long id) {
        return medicineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    @RequireAuthentication(roles = {"ADMIN"})
    public ResponseEntity<MedicineDTO.GetMedicineDTO> saveOrUpdateMedicine(@RequestBody MedicineDTO.SaveMedicineDTO medicineDTO) {
        System.out.println(medicineDTO);
        MedicineDTO.GetMedicineDTO savedMedicine = medicineService.saveOrUpdate(medicineDTO);
        return ResponseEntity.ok(savedMedicine);
    }

    @PostMapping("/delete")
    @RequireAuthentication(roles = {"ADMIN"})
    public String deleteMedicines(@RequestBody List<Long> ids) {
        return medicineService.deleteByList(ids);
    }

    @GetMapping("/by-code")
    public ResponseEntity<MedicineDTO.GetMedicineDTO> getMedicineByCode(@RequestParam String code) {
        return medicineService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name")
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> getMedicinesByName(@RequestParam String name) {
        return ResponseEntity.ok(medicineService.findByName(name));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> searchMedicines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Decimal rangePrice,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(medicineService.seach(name, categoryId, brandId, rangePrice, sortBy));
    }

    @GetMapping("/best-selling")
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> getBestSellingMedicines() {
        return ResponseEntity.ok(medicineService.getBestSaling());
    }

    @GetMapping("/newest")
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> getNewestMedicines() {
        return ResponseEntity.ok(medicineService.getMedicineNew());
    }
}