package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.MedicineMediaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MedicineMediaService {
    List<MedicineMediaDTO.GetMedicineMediaDTO> findAll();
    Optional<MedicineMediaDTO.GetMedicineMediaDTO> findById(Long id);
    MedicineMediaDTO.GetMedicineMediaDTO saveOrUpdate(MedicineMediaDTO.SaveMedicineMediaDTO medicineMediaDTO);
    String deleteByList(List<Long> ids);
    List<MedicineMediaDTO.GetMedicineMediaDTO> findByMedicineId(Long medicineId);
    Optional<MedicineMediaDTO.GetMedicineMediaDTO> findMainImageByMedicineId(Long medicineId);
    String uploadMedicineImage(MultipartFile file) throws IOException;
    String updateMedicineImage(Long mediaId, MultipartFile file) throws IOException;
    String deleteAllMediaByMedicineId(Long medicineId);
}