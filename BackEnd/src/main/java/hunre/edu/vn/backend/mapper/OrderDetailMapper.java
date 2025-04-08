package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.OrderDetailDTO;
import hunre.edu.vn.backend.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MedicineMapper.class})
public interface OrderDetailMapper {
    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "medicineId", source = "medicine.id")
    @Mapping(target = "attributeId", source = "attribute.id")
    @Mapping(target = "medicine", source = "medicine")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    OrderDetailDTO.GetOrderDetailDTO toGetOrderDetailDTO(OrderDetail entity);

    OrderDetail toOrderDetailEntity(OrderDetailDTO.SaveOrderDetailDTO dto);
}