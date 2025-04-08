package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.OrderDetailDTO;

import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    List<OrderDetailDTO.GetOrderDetailDTO> findAll();
    Optional<OrderDetailDTO.GetOrderDetailDTO> findById(Long id);
    OrderDetailDTO.GetOrderDetailDTO saveOrUpdate(OrderDetailDTO.SaveOrderDetailDTO orderDetailDTO);
    String deleteByList(List<Long> ids);
    List<OrderDetailDTO.GetOrderDetailDTO> findByOrderId(Long orderId);
}