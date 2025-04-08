package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.OrderDTO;
import hunre.edu.vn.backend.entity.Order;
import hunre.edu.vn.backend.entity.OrderStatus;
import hunre.edu.vn.backend.entity.PaymentMethod;
import hunre.edu.vn.backend.repository.OrderRepository;
import hunre.edu.vn.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;


    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO.GetOrderDTO>> getAllOrders() {
        List<OrderDTO.GetOrderDTO> orders = orderService.findAll();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/summary")
    public ResponseEntity<?> getOrderSummary() {
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        Long totalOrders = orderService.getTotalOrders();

        return ResponseEntity.ok(Map.of(
                "totalOrders", totalOrders,
                "totalRevenue", totalRevenue
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO.GetOrderDTO> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<OrderDTO.GetOrderDTO> saveOrUpdateOrder(@RequestBody OrderDTO.SaveOrderDTO orderDTO) {
        OrderDTO.GetOrderDTO savedOrder = orderService.saveOrUpdate(orderDTO);
        return ResponseEntity.ok(savedOrder);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@RequestBody List<Long> ids) {
        return orderService.deleteByList(ids);
    }

    @GetMapping("/by-order-code/{orderCode}")
    public ResponseEntity<OrderDTO.GetOrderDTO> getOrderByOrderCode(@PathVariable String orderCode) {
        return orderService.findByOrderCode(orderCode)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<OrderDTO.GetOrderDTO>> getOrdersByPatientId(@PathVariable Long patientId) {
        List<OrderDTO.GetOrderDTO> orders = orderService.findByPatientId(patientId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/cancel")
    public String cancelOrder(@RequestParam Long orderId, @RequestParam String reason) {
        return orderService.cancelOrder(orderId, reason);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<OrderDTO.GetOrderDTO>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderDTO.GetOrderDTO> orders = orderService.findByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<OrderDTO.GetOrderDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            System.out.println(orderStatus);
            OrderDTO.GetOrderDTO updatedOrder = orderService.updateStatus(orderStatus, id);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/latest-full-detail/{userId}")
    public ResponseEntity<OrderDTO.OrderFullDetailDto> getLatestOrderFullDetailByUserId(@PathVariable Long userId) {
        return orderService.findLatestOrderFullDetailByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }






//thêm mới

    @GetMapping("/revenue/weekly")
    public ResponseEntity<?> getWeeklyRevenue(@RequestParam(defaultValue = "THIS_WEEK") String period) {
        // Xác định thời gian bắt đầu và kết thúc
        LocalDateTime startDate, endDate;
        LocalDateTime now = LocalDateTime.now();

        if ("LAST_WEEK".equals(period)) {
            // Tuần trước: từ thứ 2 đến chủ nhật tuần trước
            startDate = now.minusWeeks(1)
                    .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0);
            endDate = startDate.plusDays(6)
                    .withHour(23).withMinute(59).withSecond(59);
        } else {
            // Tuần này: từ thứ 2 đến hiện tại
            startDate = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                    .withHour(0).withMinute(0).withSecond(0);
            endDate = now;
        }

        // Lấy danh sách đơn hàng trong khoảng thời gian
        List<Order> orders = orderRepository.findByStatusAndCreatedAtBetween(
                OrderStatus.COMPLETED, startDate, endDate);

        // Tạo dữ liệu cho biểu đồ
        Map<String, Object> result = processRevenueData(orders, startDate, endDate, true);

        return ResponseEntity.ok(result);
    }

    /**
     * API lấy doanh thu theo tháng (tháng này hoặc tháng trước)
     */
    @GetMapping("/revenue/monthly")
    public ResponseEntity<?> getMonthlyRevenue(@RequestParam(defaultValue = "THIS_MONTH") String period) {
        // Xác định thời gian bắt đầu và kết thúc
        LocalDateTime startDate, endDate;
        LocalDateTime now = LocalDateTime.now();

        if ("LAST_MONTH".equals(period)) {
            // Tháng trước
            startDate = now.minusMonths(1).withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0);
            endDate = now.withDayOfMonth(1).minusDays(1)
                    .withHour(23).withMinute(59).withSecond(59);
        } else {
            // Tháng này
            startDate = now.withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0);
            endDate = now;
        }

        // Lấy danh sách đơn hàng trong khoảng thời gian
        List<Order> orders = orderRepository.findByStatusAndCreatedAtBetween(
                OrderStatus.COMPLETED, startDate, endDate);

        // Tạo dữ liệu cho biểu đồ
        Map<String, Object> result = processRevenueData(orders, startDate, endDate, false);

        return ResponseEntity.ok(result);
    }

    /**
     * API lấy doanh thu theo năm (12 tháng)
     */
    @GetMapping("/revenue/yearly")
    public ResponseEntity<?> getYearlyRevenue() {
        // Xác định thời gian bắt đầu và kết thúc (1 năm)
        int year = LocalDateTime.now().getYear();
        LocalDateTime startDate = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.now();

        // Lấy danh sách đơn hàng trong khoảng thời gian
        List<Order> orders = orderRepository.findByStatusAndCreatedAtBetween(
                OrderStatus.COMPLETED, startDate, endDate);

        // Khởi tạo mảng dữ liệu hàng tháng
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenueData = new ArrayList<>();
        List<BigDecimal> profitData = new ArrayList<>();

        // Tạo dữ liệu cho từng tháng
        for (int month = 1; month <= 12; month++) {
            labels.add("Tháng " + month);

            // Lọc đơn hàng theo tháng
            int finalMonth = month;
            List<Order> monthlyOrders = orders.stream()
                    .filter(order -> order.getCreatedAt().getMonthValue() == finalMonth)
                    .collect(Collectors.toList());

            // Tính doanh thu của tháng
            BigDecimal monthlyRevenue = monthlyOrders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueData.add(monthlyRevenue);

            // Tính lợi nhuận (giả định 40% doanh thu)
            BigDecimal monthlyProfit = monthlyRevenue.multiply(new BigDecimal("0.4"));
            profitData.add(monthlyProfit);
        }

        // Tính tổng doanh thu và lợi nhuận
        BigDecimal totalRevenue = revenueData.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProfit = profitData.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo kết quả
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("revenue", revenueData);
        result.put("profit", profitData);
        result.put("totalRevenue", totalRevenue);
        result.put("totalProfit", totalProfit);
        result.put("totalOrders", orders.size());

        return ResponseEntity.ok(result);
    }

    /**
     * API lấy doanh thu theo phương thức thanh toán
     */
    @GetMapping("/revenue/by-payment-method")
    public ResponseEntity<?> getRevenueByPaymentMethod() {
        // Lấy tất cả đơn hàng đã hoàn thành
        List<Order> completedOrders = orderRepository.findByStatus(OrderStatus.COMPLETED);

        // Tạo map để lưu trữ doanh thu theo phương thức thanh toán
        Map<PaymentMethod, BigDecimal> revenueByPaymentMethod = new EnumMap<>(PaymentMethod.class);

        // Khởi tạo giá trị mặc định cho tất cả phương thức thanh toán
        for (PaymentMethod method : PaymentMethod.values()) {
            revenueByPaymentMethod.put(method, BigDecimal.ZERO);
        }

        // Tính tổng doanh thu theo phương thức thanh toán
        for (Order order : completedOrders) {
            PaymentMethod method = order.getPaymentMethod();
            BigDecimal currentRevenue = revenueByPaymentMethod.get(method);
            revenueByPaymentMethod.put(method, currentRevenue.add(order.getTotalPrice()));
        }

        return ResponseEntity.ok(revenueByPaymentMethod);
    }

    /**
     * Phương thức hỗ trợ để xử lý dữ liệu doanh thu
     */
    private Map<String, Object> processRevenueData(List<Order> orders, LocalDateTime startDate, LocalDateTime endDate, boolean isWeekly) {
        Map<String, Object> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenueData = new ArrayList<>();
        List<BigDecimal> profitData = new ArrayList<>();

        // Tạo danh sách các ngày trong khoảng thời gian
        List<LocalDate> dateRange = new ArrayList<>();
        LocalDate currentDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        while (!currentDate.isAfter(endLocalDate)) {
            dateRange.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        // Tạo labels và khởi tạo dữ liệu
        for (LocalDate date : dateRange) {
            // Tạo label cho ngày (định dạng khác nhau cho tuần và tháng)
            String label;
            if (isWeekly) {
                // Định dạng cho tuần (T2, T3, ...)
                label = date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, Locale.forLanguageTag("vi")).toUpperCase();
            } else {
                // Định dạng cho tháng (01/04, 02/04, ...)
                label = date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM"));
            }
            labels.add(label);

            // Tính doanh thu cho ngày
            final LocalDate currentDateFinal = date;
            BigDecimal dailyRevenue = orders.stream()
                    .filter(order -> order.getCreatedAt().toLocalDate().equals(currentDateFinal))
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenueData.add(dailyRevenue);

            // Tính lợi nhuận (giả định 40% doanh thu)
            BigDecimal dailyProfit = dailyRevenue.multiply(new BigDecimal("0.4"));
            profitData.add(dailyProfit);
        }

        // Tính tổng doanh thu và lợi nhuận
        BigDecimal totalRevenue = revenueData.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProfit = profitData.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // Thêm dữ liệu vào kết quả
        result.put("labels", labels);
        result.put("revenue", revenueData);
        result.put("profit", profitData);
        result.put("totalRevenue", totalRevenue);
        result.put("totalProfit", totalProfit);
        result.put("totalOrders", orders.size());

        return result;
    }

}