package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Attribute;
import hunre.edu.vn.backend.entity.Medicine;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttributeRepository extends BaseRepository<Attribute> {
    @Query("SELECT a FROM Attribute as a WHERE a.isDeleted = false")
    List<Attribute> findByMedicineId(Long medicineId);
}
