package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.DiscountDTO;

import java.util.List;
import java.util.Optional;

public interface DiscountService {
    List<DiscountDTO.GetDiscountDTO> findAll();
    Optional<DiscountDTO.GetDiscountDTO> findById(Long id);
    DiscountDTO.GetDiscountDTO saveOrUpdate(DiscountDTO.SaveDiscountDTO discountDTO);
    String deleteByList(List<Long> ids);
    Optional<DiscountDTO.GetDiscountDTO> findByCode(String code);
    List<DiscountDTO.GetDiscountDTO> findByMedicineId(Long medicineId);
}