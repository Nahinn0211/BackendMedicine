package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Category;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.MedicineCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineCategoryRepository extends BaseRepository<MedicineCategory> {
    @Query("SELECT mc FROM MedicineCategory mc WHERE mc.medicine.id = ?1 AND mc.isDeleted = false")
    List<MedicineCategory> findByMedicineId(Long medicineId);
}
