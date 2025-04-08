package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Brand;
import hunre.edu.vn.backend.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends BaseRepository<Medicine> {
    @Query("SELECT m FROm Medicine as m WHERE m.code = :code AND m.isDeleted = false")
    Optional<Medicine> findByCode(String code);
    @Query("SELECT m FROm Medicine as m WHERE m.name = :name AND m.isDeleted = false")
    List<Medicine> findByName(String name);
}
