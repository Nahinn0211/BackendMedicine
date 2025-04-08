package hunre.edu.vn.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hunre.edu.vn.backend.dto.MedicineMediaDTO;
import hunre.edu.vn.backend.service.MedicineMediaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicine-media")
public class MedicineMediaController {

    private final MedicineMediaService medicineMediaService;

    public MedicineMediaController(MedicineMediaService medicineMediaService) {
        this.medicineMediaService = medicineMediaService;
    }

    @GetMapping
    public ResponseEntity<List<MedicineMediaDTO.GetMedicineMediaDTO>> getAllMedicineMedia() {
        List<MedicineMediaDTO.GetMedicineMediaDTO> mediaList = medicineMediaService.findAll();
        return ResponseEntity.ok(mediaList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineMediaDTO.GetMedicineMediaDTO> getMedicineMediaById(@PathVariable Long id) {
        return medicineMediaService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value= "/save", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicineMediaDTO.GetMedicineMediaDTO> saveOrUpdateMedicineMedia(
            @RequestPart("media") String mediaJson,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MedicineMediaDTO.SaveMedicineMediaDTO medicineMediaDto = objectMapper.readValue(mediaJson, MedicineMediaDTO.SaveMedicineMediaDTO.class);

            if (medicineMediaDto.getId() != null && medicineMediaDto.getId() > 0) {
                Optional<MedicineMediaDTO.GetMedicineMediaDTO> existingMediaOpt = medicineMediaService.findById(medicineMediaDto.getId());
                if (existingMediaOpt.isPresent()) {
                    if (file != null && !file.isEmpty()) {
                        String newImageUrl = medicineMediaService.updateMedicineImage(
                                medicineMediaDto.getId(), file);
                        medicineMediaDto.setMediaUrl(newImageUrl);
                    } else {
                        medicineMediaDto.setMediaUrl(existingMediaOpt.get().getMediaUrl());
                    }
                    medicineMediaDto.setUpdatedAt(LocalDateTime.now());
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
            else {
                if (file != null && !file.isEmpty()) {
                    String newImageUrl = medicineMediaService.uploadMedicineImage(file);
                    medicineMediaDto.setMediaUrl(newImageUrl);
                } else {
                    return ResponseEntity.badRequest().body(null);
                }
                medicineMediaDto.setCreatedAt(LocalDateTime.now());
                medicineMediaDto.setUpdatedAt(LocalDateTime.now());
            }

            MedicineMediaDTO.GetMedicineMediaDTO savedMedia = medicineMediaService.saveOrUpdate(medicineMediaDto);
            return ResponseEntity.ok(savedMedia);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public String deleteMedicineMedia(@RequestBody List<Long> ids) {
        return medicineMediaService.deleteByList(ids);
    }

    @GetMapping("/by-medicine/{medicineId}")
    public ResponseEntity<List<MedicineMediaDTO.GetMedicineMediaDTO>> getMediaByMedicineId(@PathVariable Long medicineId) {
        List<MedicineMediaDTO.GetMedicineMediaDTO> mediaList = medicineMediaService.findByMedicineId(medicineId);
        return ResponseEntity.ok(mediaList);
    }

    @GetMapping("/main-image/{medicineId}")
    public ResponseEntity<MedicineMediaDTO.GetMedicineMediaDTO> getMainImageByMedicineId(@PathVariable Long medicineId) {
        return medicineMediaService.findMainImageByMedicineId(medicineId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}