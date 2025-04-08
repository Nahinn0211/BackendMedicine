package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.Order;
import hunre.edu.vn.backend.entity.OrderDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends BaseRepository<OrderDetail> {

    @Query("SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od WHERE od.medicine.id = :medicineId AND od.isDeleted = false")
    long sumQuantityByMedicineId(@Param("medicineId") Long medicineId);
    @Query("SELECT od FROM OrderDetail od WHERE od.order.id = :orderId AND od.isDeleted = false")
    List<OrderDetail> findByOrder_Id(Long orderId);
}