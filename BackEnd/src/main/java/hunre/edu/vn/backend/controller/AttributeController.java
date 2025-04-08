package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.AttributeDTO;
import hunre.edu.vn.backend.service.AttributeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
public class AttributeController {

    private final AttributeService attributeService;

    public AttributeController(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @GetMapping
    public ResponseEntity<List<AttributeDTO.GetAttributeDTO>> getAllAttributes() {
        List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findAll();
        return ResponseEntity.ok(attributes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttributeDTO.GetAttributeDTO> getAttributeById(@PathVariable Long id) {
        return attributeService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<List<AttributeDTO.GetAttributeDTO>> getAttributeByMedicineId(@PathVariable Long medicineId) {
        List<AttributeDTO.GetAttributeDTO> attributes = attributeService.findByMedicineId(medicineId);
        if (attributes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attributes);
    }

    @PostMapping("/save")
    public ResponseEntity<AttributeDTO.GetAttributeDTO> saveOrUpdateAttribute(@RequestBody AttributeDTO.SaveAttributeDTO attributeDTO) {
        AttributeDTO.GetAttributeDTO savedAttribute = attributeService.saveOrUpdate(attributeDTO);
        return ResponseEntity.ok(savedAttribute);
    }

    @DeleteMapping("/{id}")
    public String deleteAttribute(@RequestBody List<Long> ids) {
        return attributeService.deleteByList(ids);
    }
}