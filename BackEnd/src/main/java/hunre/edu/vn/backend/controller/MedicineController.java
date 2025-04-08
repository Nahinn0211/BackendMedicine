package hunre.edu.vn.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hunre.edu.vn.backend.annotation.RequireAuthentication;
import hunre.edu.vn.backend.dto.MedicineDTO;
import hunre.edu.vn.backend.dto.MedicineMediaDTO;
import hunre.edu.vn.backend.service.MedicineCategoryService;
import hunre.edu.vn.backend.service.MedicineMediaService;
import hunre.edu.vn.backend.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.ion.Decimal;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@Tag(name = "Medicine Management", description = "API to manage medicines")
public class MedicineController {

    private final MedicineService medicineService;
    private final MedicineMediaService medicineMediaService;
    private final MedicineCategoryService medicineCategoryService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Get all medicines", description = "Returns a list of all medicines")
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> getAllMedicines() {
        return ResponseEntity.ok(medicineService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID", description = "Returns a medicine by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved medicine"),
            @ApiResponse(responseCode = "404", description = "Medicine not found")
    })
    public ResponseEntity<MedicineDTO.GetMedicineDTO> getMedicineById(@PathVariable Long id) {
        return medicineService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * API chính để lưu/cập nhật thuốc và tất cả thông tin liên quan
     * (thuộc tính, danh mục, hình ảnh) trong một lần gọi
     */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequireAuthentication(roles = {"ADMIN"})
    @Operation(summary = "Save medicine with all details",
            description = "Creates or updates a medicine with all related data (attributes, categories, images)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicine saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Server error during processing")
    })
    public ResponseEntity<?> saveMedicine(
            @RequestParam("medicineJson") String medicineJson,
            @RequestParam(value = "categoryIds", required = false) String categoryIdsJson,
            @RequestParam(value = "mainImageIndex", required = false, defaultValue = "0") String mainImageIndexStr,
            @RequestParam(value = "images", required = false) MultipartFile[] files) {

        try {
            // Parse medicineJson thành đối tượng DTO
            MedicineDTO.SaveMedicineDTO medicineDTO = objectMapper.readValue(medicineJson, MedicineDTO.SaveMedicineDTO.class);

            // Xử lý categoryIds
            List<Long> categoryIds = new ArrayList<>();
            if (categoryIdsJson != null && !categoryIdsJson.isEmpty()) {
                try {
                    // Thử parse như mảng JSON
                    Long[] idsArray = objectMapper.readValue(categoryIdsJson, Long[].class);
                    categoryIds = Arrays.asList(idsArray);
                } catch (Exception e) {
                    // Fallback: parse như danh sách phân cách bởi dấu phẩy
                    categoryIds = Arrays.stream(categoryIdsJson.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::parseLong)
                            .collect(Collectors.toList());
                }
            }

            // Xử lý mainImageIndex
            Integer mainImageIndex = 0;
            if (mainImageIndexStr != null && !mainImageIndexStr.isEmpty()) {
                mainImageIndex = Integer.parseInt(mainImageIndexStr);
            }

            // Gọi service để xử lý logic
            Map<String, Object> result = medicineService.saveOrUpdateMedicineWithDetails(
                    medicineDTO, files, categoryIds, mainImageIndex);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage(), "type", e.getClass().getSimpleName()));
        }
    }

    @PostMapping("/delete")
    @RequireAuthentication(roles = {"ADMIN"})
    @Operation(summary = "Delete medicines", description = "Deletes medicines by IDs")
    public ResponseEntity<String> deleteMedicines(@RequestBody List<Long> ids) {
        String result = medicineService.deleteByList(ids);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @Operation(summary = "Search medicines", description = "Search medicines by various criteria")
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> searchMedicines(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Decimal rangePrice,
            @RequestParam(required = false) String sortBy) {

        // Nếu truyền code, ưu tiên tìm theo code
        if (code != null && !code.trim().isEmpty()) {
            return medicineService.findByCode(code)
                    .map(medicine -> ResponseEntity.ok(List.of(medicine)))
                    .orElseGet(() -> ResponseEntity.ok(List.of()));
        }

        // Nếu truyền tên, ưu tiên tìm theo tên
        if (name != null && !name.trim().isEmpty()) {
            return ResponseEntity.ok(medicineService.findByName(name));
        }

        // Tìm kiếm theo các tiêu chí khác
        return ResponseEntity.ok(medicineService.seach(name, categoryId, brandId, rangePrice, sortBy));
    }

    @GetMapping("/best-selling")
    @Operation(summary = "Get best-selling medicines", description = "Returns the top 10 best-selling medicines")
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> getBestSellingMedicines() {
        return ResponseEntity.ok(medicineService.getBestSaling());
    }

    @GetMapping("/newest")
    @Operation(summary = "Get newest medicines", description = "Returns the 10 newest medicines")
    public ResponseEntity<List<MedicineDTO.GetMedicineDTO>> getNewestMedicines() {
        return ResponseEntity.ok(medicineService.getMedicineNew());
    }

    // API bổ sung cho Media và Category

    @GetMapping("/{id}/details")
    @Operation(summary = "Get medicine details", description = "Returns complete medicine details including media and categories")
    public ResponseEntity<?> getMedicineDetails(@PathVariable Long id) {
        return medicineService.findById(id)
                .map(medicine -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("medicine", medicine);

                    // Đảm bảo thuộc tính được lấy đầy đủ trong medicine
                    // Không cần tải thêm attributes vì đã được lấy trong findById

                    // Thông tin bổ sung
                    result.put("medias", medicineMediaService.findByMedicineId(id));
                    result.put("categories", medicineCategoryService.findMedicineCategoryDtoByMedicineId(id));

                    return ResponseEntity.ok(result);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}