package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.MedicineMedia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineMediaRepository extends BaseRepository<MedicineMedia> {

    @Query("SELECT mm FROM MedicineMedia mm WHERE mm.medicine.id = ?1 AND mm.isDeleted = false")
    List<MedicineMedia> findByMedicineId(Long medicineId);

    @Query("SELECT mm FROM MedicineMedia mm WHERE mm.medicine.id = ?1 AND mm.mainImage = true AND mm.isDeleted = false")
    Optional<MedicineMedia> findMainImageByMedicineId(Long medicineId);

}