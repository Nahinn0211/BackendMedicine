package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.PatientProfile;
import hunre.edu.vn.backend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientProfileRepository extends BaseRepository<PatientProfile> {

    @Query("SELECT pf FROM PatientProfile pf WHERE pf.user.id = :userId AND pf.isDeleted = false")
    Optional<PatientProfile> findByUserId(Long userId);

    @Query("SELECT count(pf) FROM PatientProfile pf WHERE pf.isDeleted = false")
    long count();
}