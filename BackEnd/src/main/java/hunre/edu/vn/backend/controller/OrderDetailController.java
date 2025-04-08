package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.OrderDetailDTO;
import hunre.edu.vn.backend.service.OrderDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDetailDTO.GetOrderDetailDTO>> getAllOrderDetails() {
        List<OrderDetailDTO.GetOrderDetailDTO> orderDetails = orderDetailService.findAll();
        return ResponseEntity.ok(orderDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDTO.GetOrderDetailDTO> getOrderDetailById(@PathVariable Long id) {
        return orderDetailService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<OrderDetailDTO.GetOrderDetailDTO> saveOrUpdateOrderDetail(@RequestBody OrderDetailDTO.SaveOrderDetailDTO orderDetailDTO) {
        OrderDetailDTO.GetOrderDetailDTO savedOrderDetail = orderDetailService.saveOrUpdate(orderDetailDTO);
        return ResponseEntity.ok(savedOrderDetail);
    }

    @DeleteMapping("/{id}")
    public String deleteOrderDetail(@RequestBody List<Long> ids) {
        return orderDetailService.deleteByList(ids);
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<List<OrderDetailDTO.GetOrderDetailDTO>> getOrderDetailsByOrderId(@PathVariable Long orderId) {
        List<OrderDetailDTO.GetOrderDetailDTO> orderDetails = orderDetailService.findByOrderId(orderId);
        return ResponseEntity.ok(orderDetails);
    }
}