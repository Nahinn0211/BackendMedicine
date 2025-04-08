package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.DiscountDTO;
import hunre.edu.vn.backend.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping
    public ResponseEntity<List<DiscountDTO.GetDiscountDTO>> getAllDiscounts() {
        List<DiscountDTO.GetDiscountDTO> discounts = discountService.findAll();
        return ResponseEntity.ok(discounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscountDTO.GetDiscountDTO> getDiscountById(@PathVariable Long id) {
        return discountService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<DiscountDTO.GetDiscountDTO> saveOrUpdateDiscount(@RequestBody DiscountDTO.SaveDiscountDTO discountDTO) {
        DiscountDTO.GetDiscountDTO savedDiscount = discountService.saveOrUpdate(discountDTO);
        return ResponseEntity.ok(savedDiscount);
    }

    @DeleteMapping("/{id}")
    public String deleteDiscount(@RequestBody List<Long> ids) {
        return discountService.deleteByList(ids);
    }

    @GetMapping("/search/code/{code}")
    public ResponseEntity<DiscountDTO.GetDiscountDTO> findByCode(@PathVariable String code) {
        return discountService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search/medicine/{medicineId}")
    public ResponseEntity<List<DiscountDTO.GetDiscountDTO>> findByMedicineId(@PathVariable Long medicineId) {
        List<DiscountDTO.GetDiscountDTO> discounts = discountService.findByMedicineId(medicineId);
        return ResponseEntity.ok(discounts);
    }
}