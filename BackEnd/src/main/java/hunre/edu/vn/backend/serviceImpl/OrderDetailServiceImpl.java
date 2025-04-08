package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.OrderDetailDTO;
import hunre.edu.vn.backend.entity.*;
import hunre.edu.vn.backend.mapper.OrderDetailMapper;
import hunre.edu.vn.backend.repository.*;
import hunre.edu.vn.backend.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final MedicineRepository medicineRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final AttributeRepository attributeRepository;
    private final MedicineMediaRepository medicineMediaRepository;

    @Autowired
    public OrderDetailServiceImpl(
            OrderDetailRepository orderDetailRepository,
            OrderRepository orderRepository,
            MedicineRepository medicineRepository,
            OrderDetailMapper orderDetailMapper, AttributeRepository attributeRepository, MedicineMediaRepository medicineMediaRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.medicineRepository = medicineRepository;
        this.orderDetailMapper = orderDetailMapper;
        this.attributeRepository = attributeRepository;
        this.medicineMediaRepository = medicineMediaRepository;
    }

    @Override
    public List<OrderDetailDTO.GetOrderDetailDTO> findAll() {
        return orderDetailRepository.findAllActive()
                .stream()
                .map(orderDetailMapper::toGetOrderDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderDetailDTO.GetOrderDetailDTO> findById(Long id) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.findActiveById(id);
        return orderDetail.map(orderDetailMapper::toGetOrderDetailDTO);
    }

    @Override
    @Transactional
    public OrderDetailDTO.GetOrderDetailDTO saveOrUpdate(OrderDetailDTO.SaveOrderDetailDTO orderDetailDTO) {
        OrderDetail orderDetail;

        if (orderDetailDTO.getId() == null || orderDetailDTO.getId() == 0) {
            // INSERT case
            orderDetail = new OrderDetail();
            orderDetail.setCreatedAt(LocalDateTime.now());
            orderDetail.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<OrderDetail> existingOrderDetail = orderDetailRepository.findActiveById(orderDetailDTO.getId());
            if (existingOrderDetail.isEmpty()) {
                throw new RuntimeException("Order detail not found with ID: " + orderDetailDTO.getId());
            }
            orderDetail = existingOrderDetail.get();
            orderDetail.setUpdatedAt(LocalDateTime.now());
        }

        // Xử lý order relationship
        Order order = orderRepository.findActiveById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderDetailDTO.getOrderId()));
        orderDetail.setOrder(order);

        // Xử lý medicine relationship
        Medicine medicine = medicineRepository.findActiveById(orderDetailDTO.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + orderDetailDTO.getMedicineId()));
        orderDetail.setMedicine(medicine);

        Attribute attribute = attributeRepository.findActiveById(orderDetailDTO.getAttributeId())
                .orElseThrow(() -> new RuntimeException("Attribute not found with ID: " + orderDetailDTO.getAttributeId()));
        orderDetail.setAttribute(attribute);
        int stock = attribute.getStock();
        stock -= orderDetailDTO.getQuantity();
        orderDetail.setQuantity(stock);
        // Cập nhật các trường khác
        orderDetail.setQuantity(orderDetailDTO.getQuantity());
        orderDetail.setUnitPrice(orderDetailDTO.getUnitPrice());
        attributeRepository.save(attribute);
        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);
        return orderDetailMapper.toGetOrderDetailDTO(savedOrderDetail);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for(Long id : ids) {
            if (orderDetailRepository.existsById(id)) {
                orderDetailRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " + ids.size() + " chi tiết đơn hàng";
    }

    @Override
    public List<OrderDetailDTO.GetOrderDetailDTO> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrder_Id(orderId)
                .stream()
                .map(orderDetailMapper::toGetOrderDetailDTO)
                .collect(Collectors.toList());
    }
}