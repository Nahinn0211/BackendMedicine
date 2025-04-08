package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.MedicineDTO;
import software.amazon.ion.Decimal;

import java.util.List;
import java.util.Optional;

public interface MedicineService {
    List<MedicineDTO.GetMedicineDTO> findAll();
    Optional<MedicineDTO.GetMedicineDTO> findById(Long id);
    MedicineDTO.GetMedicineDTO saveOrUpdate(MedicineDTO.SaveMedicineDTO medicineDTO);
    String deleteByList(List<Long> ids);
    Optional<MedicineDTO.GetMedicineDTO> findByCode(String code);
    List<MedicineDTO.GetMedicineDTO> findByName(String name);
    List<MedicineDTO.GetMedicineDTO> seach(String name, Long categoryId, Long brandiId, Decimal RangePrice, String SortBy);
    List<MedicineDTO.GetMedicineDTO> getBestSaling();
    List<MedicineDTO.GetMedicineDTO> getMedicineNew();
}