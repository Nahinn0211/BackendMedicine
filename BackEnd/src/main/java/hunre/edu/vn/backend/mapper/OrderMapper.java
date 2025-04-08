package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.OrderDTO;
import hunre.edu.vn.backend.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    OrderDTO.GetOrderDTO toGetOrderDTO(Order entity);

    Order toOrderEntity(OrderDTO.SaveOrderDTO dto);

    @Mapping(target = "orderDetails", source = "orderDetails")
    OrderDTO.GetOrderDTO toGetOrderDtos(Order order);
}