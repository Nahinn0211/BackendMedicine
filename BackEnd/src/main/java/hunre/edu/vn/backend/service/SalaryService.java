package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.SalaryDTO;

import java.util.List;
import java.util.Optional;

public interface SalaryService {
    List<SalaryDTO.GetSalaryDTO> findAll();
    Optional<SalaryDTO.GetSalaryDTO> findById(Long id);
    SalaryDTO.GetSalaryDTO saveOrUpdate(SalaryDTO.SaveSalaryDTO salaryDTO);
    String deleteByList(List<Long> ids);
    List<SalaryDTO.GetSalaryDTO> findByUserId(Long userId);
}