package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.VoucherDTO;

import java.util.List;
import java.util.Optional;

public interface VouchersService {
    List<VoucherDTO.GetVoucherDTO> findAll();
    Optional<VoucherDTO.GetVoucherDTO> findById(Long id);
    VoucherDTO.GetVoucherDTO saveOrUpdate(VoucherDTO.SaveVoucherDTO VoucherDTO);
    String deleteByList(List<Long> ids);
    Optional<VoucherDTO.GetVoucherDTO> findByCode(String code);
}