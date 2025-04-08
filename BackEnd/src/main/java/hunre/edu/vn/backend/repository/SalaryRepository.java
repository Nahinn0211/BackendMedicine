package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.PaymentStatus;
import hunre.edu.vn.backend.entity.Salary;
import hunre.edu.vn.backend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalaryRepository extends BaseRepository<Salary> {

    @Query("SELECT s FROM Salary s WHERE s.user.id = ?1 AND s.isDeleted = false ORDER BY s.createdAt DESC")
    List<Salary> findByUserId(Long userId);
}
