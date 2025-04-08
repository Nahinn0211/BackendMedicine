package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.VoucherDTO;
import hunre.edu.vn.backend.service.VouchersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VouchersController {

    private final VouchersService voucherService;

    public VouchersController(VouchersService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<List<VoucherDTO.GetVoucherDTO>> getAllVouchers() {
        List<VoucherDTO.GetVoucherDTO> vouchers = voucherService.findAll();
        return ResponseEntity.ok(vouchers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherDTO.GetVoucherDTO> getVoucherById(@PathVariable Long id) {
        return voucherService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<VoucherDTO.GetVoucherDTO> saveOrUpdateVoucher(@RequestBody VoucherDTO.SaveVoucherDTO voucherDTO) {
        VoucherDTO.GetVoucherDTO savedVoucher = voucherService.saveOrUpdate(voucherDTO);
        return ResponseEntity.ok(savedVoucher);
    }

    @DeleteMapping("/{id}")
    public String deleteVoucher(@RequestBody List<Long> ids) {
        return voucherService.deleteByList(ids);
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<VoucherDTO.GetVoucherDTO> getVoucherByCode(@PathVariable String code) {
        return voucherService.findByCode(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}