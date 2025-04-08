package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends BaseRepository<DoctorProfile> {
    @Query("SELECT df FROM DoctorProfile as df WHERE df.user.id = :userId AND df.isDeleted = false")
    Optional<DoctorProfile> findByUser_Id(Long userId);
    @Query("SELECT count(df) FROM DoctorProfile as df WHERE df.isDeleted = false")
    long count();
}