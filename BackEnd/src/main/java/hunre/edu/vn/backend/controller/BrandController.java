package hunre.edu.vn.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hunre.edu.vn.backend.dto.BrandDTO;
import hunre.edu.vn.backend.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<BrandDTO.GetBrandDTO>> getAllBrands() {
        List<BrandDTO.GetBrandDTO> brands = brandService.findAll();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO.GetBrandDTO> getBrandById(@PathVariable Long id) {
        return brandService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BrandDTO.GetBrandDTO> saveOrUpdateBrand(@RequestPart("brand") String brandJson,@RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BrandDTO.SaveBrandDTO brandDto = objectMapper.readValue(brandJson, BrandDTO.SaveBrandDTO.class);

            if (brandDto.getId() != null) {
                Optional<BrandDTO.GetBrandDTO> existingBrandOpt = brandService.findById(brandDto.getId());
                if (existingBrandOpt.isPresent()) {
                    BrandDTO.GetBrandDTO existingBrand = existingBrandOpt.get();
                    if (file != null && !file.isEmpty()) {
                        brandService.deleteBrandImage(existingBrand.getImage());
                        String newImageUrl = brandService.uploadBrandImage(file);
                        brandDto.setImage(newImageUrl);
                    } else {
                        brandDto.setImage(existingBrand.getImage());
                    }
                    brandDto.setUpdatedAt(LocalDateTime.now());
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                // New brand
                if (file != null && !file.isEmpty()) {
                    String newImageUrl = brandService.uploadBrandImage(file);
                    brandDto.setImage(newImageUrl);
                }
                brandDto.setCreatedAt(LocalDateTime.now());
                brandDto.setUpdatedAt(LocalDateTime.now());
            }

            BrandDTO.GetBrandDTO savedBrand = brandService.saveOrUpdate(brandDto);

            return ResponseEntity.ok(savedBrand);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public String deleteBrand(@RequestBody List<Long> ids) {
        return brandService.deleteByList(ids);
    }

    @GetMapping("/search/name")
    public ResponseEntity<BrandDTO.GetBrandDTO> findByName(@RequestParam String name) {
        return brandService.findByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
