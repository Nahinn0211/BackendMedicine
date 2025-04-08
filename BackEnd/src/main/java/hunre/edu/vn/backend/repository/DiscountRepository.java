package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Discount;
import hunre.edu.vn.backend.entity.Medicine;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends BaseRepository<Discount> {
    @Query("SELECT d FROM Discount as d WHERE d.code = :code AND d.isDeleted = false")
    Optional<Discount> findByCode(String code);
    @Query("SELECT d FROM Discount as d WHERE d.medicine.id = :medicineId AND d.isDeleted = false")
    List<Discount> findByMedicine_Id(Long medicineId);
}
