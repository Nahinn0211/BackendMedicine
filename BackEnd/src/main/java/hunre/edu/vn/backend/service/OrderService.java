package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.OrderDTO;
import hunre.edu.vn.backend.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderDTO.GetOrderDTO> findAll();
    Optional<OrderDTO.GetOrderDTO> findById(Long id);
    OrderDTO.GetOrderDTO saveOrUpdate(OrderDTO.SaveOrderDTO orderDTO);
    String deleteByList(List<Long> ids);
    Optional<OrderDTO.GetOrderDTO> findByOrderCode(String orderCode);
    List<OrderDTO.GetOrderDTO> findByPatientId(Long patientId);
    List<OrderDTO.GetOrderDTO> findByStatus(OrderStatus status);
    OrderDTO.GetOrderDTO updateStatus(OrderStatus status, Long id);
    Optional<OrderDTO.OrderFullDetailDto> findLatestOrderFullDetailByUserId(Long userId);
    BigDecimal getTotalRevenue();
    Long getTotalOrders();
    String cancelOrder(Long id, String reason);
}