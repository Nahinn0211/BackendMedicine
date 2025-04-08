package hunre.edu.vn.backend.mapper;


import hunre.edu.vn.backend.dto.DiscountDTO;
import hunre.edu.vn.backend.entity.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DiscountMapper {
    @Mapping(target = "medicineId", source = "medicine.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    DiscountDTO.GetDiscountDTO toGetDiscountDTO(Discount entity);

    Discount toEntity(DiscountDTO.SaveDiscountDTO dto);
}