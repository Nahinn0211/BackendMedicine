package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.VoucherDTO;
import hunre.edu.vn.backend.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VouchersMapper {
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    VoucherDTO.GetVoucherDTO toGetDTO(Voucher entity);

    Voucher toEntity(VoucherDTO.SaveVoucherDTO dto);
}