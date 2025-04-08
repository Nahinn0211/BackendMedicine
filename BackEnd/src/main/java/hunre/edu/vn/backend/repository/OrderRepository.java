package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Order;
import hunre.edu.vn.backend.entity.OrderStatus;
import hunre.edu.vn.backend.entity.PatientProfile;
import hunre.edu.vn.backend.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends BaseRepository<Order> {

    @Query("SELECT o FROM Order o WHERE o.orderCode = :orderCode AND o.isDeleted = false")
    Optional<Order> findByOrderCode(@Param("orderCode") String orderCode);

    @Query("SELECT o FROM Order o WHERE o.patient.id = :patientId AND o.isDeleted = false")
    List<Order> findByPatient_Id(@Param("patientId") Long patientId);

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.isDeleted = false")
    List<Order> findByStatus(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o " +
            "JOIN o.patient p " +
            "JOIN p.user u " +
            "WHERE u.id = :userId AND o.isDeleted = false " +
            "ORDER BY o.createdAt DESC")
    List<Order> findLatestOrdersByUserId(@Param("userId") Long userId, Pageable pageable);

    default Optional<Order> findTopByPatient_User_IdOrderByCreatedAtDesc(Long userId) {
        Pageable firstPageWithOneElement = PageRequest.of(0, 1);
        List<Order> orders = findLatestOrdersByUserId(userId, firstPageWithOneElement);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.isDeleted = false")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.isDeleted = false")
    Long countTotalOrders();

    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startDateTime AND :endDateTime AND o.isDeleted = false")
    List<Order> findByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startDateTime AND :endDateTime AND o.isDeleted = false")
    Long countByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startDate AND :endDate AND o.isDeleted = false")
    BigDecimal sumTotalPriceByStatusAndDateRange(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}