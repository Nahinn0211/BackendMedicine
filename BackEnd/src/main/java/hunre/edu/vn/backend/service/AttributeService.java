package hunre.edu.vn.backend.service;
import hunre.edu.vn.backend.dto.AttributeDTO;

import java.util.List;
import java.util.Optional;

public interface AttributeService {
    List<AttributeDTO.GetAttributeDTO> findAll();
    Optional<AttributeDTO.GetAttributeDTO> findById(Long id);
    AttributeDTO.GetAttributeDTO saveOrUpdate(AttributeDTO.SaveAttributeDTO attributeDTO);
    String deleteByList(List<Long> ids);
    List<AttributeDTO.GetAttributeDTO> findByMedicineId(Long id);
}