package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.VoucherDTO;
import hunre.edu.vn.backend.entity.Voucher;
import hunre.edu.vn.backend.mapper.VouchersMapper;
import hunre.edu.vn.backend.repository.VoucherRepository;
import hunre.edu.vn.backend.service.VouchersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VouchersServiceImpl implements VouchersService {
    private final VouchersMapper vouchersMapper;
    private final VoucherRepository VouchersRepository;
    private final VoucherRepository voucherRepository;

    public VouchersServiceImpl(VoucherRepository VouchersRepository, VouchersMapper vouchersMapper, VoucherRepository voucherRepository) {
        this.VouchersRepository = VouchersRepository;
        this.vouchersMapper = vouchersMapper;
        this.voucherRepository = voucherRepository;
    }

    @Override
    public List<VoucherDTO.GetVoucherDTO> findAll() {
        return VouchersRepository.findAll()
                .stream()
                .map(vouchersMapper::toGetDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<VoucherDTO.GetVoucherDTO> findById(Long id) {
        return VouchersRepository.findById(id).map(vouchersMapper::toGetDTO);
    }

    @Override
    @Transactional
    public VoucherDTO.GetVoucherDTO saveOrUpdate(VoucherDTO.SaveVoucherDTO VoucherDTO) {
        Voucher voucher;

        if (VoucherDTO.getId() == null || VoucherDTO.getId() == 0) {
            // INSERT case
            voucher = vouchersMapper.toEntity(VoucherDTO);
            voucher.setCreatedAt(LocalDateTime.now());
            voucher.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Voucher> existingVouchers = VouchersRepository.findById(VoucherDTO.getId());
            if (existingVouchers.isEmpty()) {
                throw new RuntimeException("Vouchers not found with ID: " + VoucherDTO.getId());
            }

            voucher = existingVouchers.get();
            voucher.setCode(VoucherDTO.getCode());
            voucher.setVoucherPercentage(VoucherDTO.getVoucherPercentage());
            voucher.setStock(VoucherDTO.getStock());
            voucher.setStartDate(VoucherDTO.getStartDate());
            voucher.setEndDate(VoucherDTO.getEndDate());
            voucher.setUpdatedAt(LocalDateTime.now());
        }

        Voucher savedVouchers = VouchersRepository.save(voucher);
        return vouchersMapper.toGetDTO(savedVouchers);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (voucherRepository.existsById(id)){
                voucherRepository.softDelete(id);
            }
        }

        return "Đã xóa " + ids.size() + " mã giảm giá";
    }

    @Override
    public Optional<VoucherDTO.GetVoucherDTO> findByCode(String code) {
        return VouchersRepository.findByCodeAndIsDeletedFalse(code).map(vouchersMapper::toGetDTO);
    }
}