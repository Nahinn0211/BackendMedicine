package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.SalaryDTO;
import hunre.edu.vn.backend.service.SalaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    public ResponseEntity<List<SalaryDTO.GetSalaryDTO>> getAllSalaries() {
        List<SalaryDTO.GetSalaryDTO> salaries = salaryService.findAll();
        return ResponseEntity.ok(salaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryDTO.GetSalaryDTO> getSalaryById(@PathVariable Long id) {
        return salaryService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<SalaryDTO.GetSalaryDTO> saveOrUpdateSalary(@RequestBody SalaryDTO.SaveSalaryDTO salaryDTO) {
        SalaryDTO.GetSalaryDTO savedSalary = salaryService.saveOrUpdate(salaryDTO);
        return ResponseEntity.ok(savedSalary);
    }

    @DeleteMapping("/{id}")
    public String deleteSalary(@RequestBody List<Long> ids) {
        return salaryService.deleteByList(ids);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<SalaryDTO.GetSalaryDTO>> getSalariesByUserId(@PathVariable Long userId) {
        List<SalaryDTO.GetSalaryDTO> salaries = salaryService.findByUserId(userId);
        return ResponseEntity.ok(salaries);
    }
}