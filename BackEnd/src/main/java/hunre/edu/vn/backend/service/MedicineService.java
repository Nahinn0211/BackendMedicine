package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.MedicineDTO;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.ion.Decimal;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MedicineService {
    List<MedicineDTO.GetMedicineDTO> findAll();

    Optional<MedicineDTO.GetMedicineDTO> findById(Long id);

    MedicineDTO.GetMedicineDTO saveOrUpdate(MedicineDTO.SaveMedicineDTO medicineDTO);

    String deleteByList(List<Long> ids);

    Optional<MedicineDTO.GetMedicineDTO> findByCode(String code);

    List<MedicineDTO.GetMedicineDTO> findByName(String name);

    List<MedicineDTO.GetMedicineDTO> seach(String name, Long categoryId, Long brandId, Decimal rangePrice, String sortBy);

    List<MedicineDTO.GetMedicineDTO> getBestSaling();

    List<MedicineDTO.GetMedicineDTO> getMedicineNew();


    Map<String, Object> saveOrUpdateMedicineWithDetails(
            MedicineDTO.SaveMedicineDTO medicineDTO,
            MultipartFile[] files,
            List<Long> categoryIds,
            Integer mainImageIndex) throws IOException;
}