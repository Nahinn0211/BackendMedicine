package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.Review;
import hunre.edu.vn.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends BaseRepository<Review> {
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.isDeleted = false")
    List<Review> findByUser_Id(Long userId);
    @Query("SELECT r FROM Review r WHERE r.rating = :rating AND r.isDeleted = false")
    List<Review> findByRating(Integer rating);
    @Query("SELECT r FROM Review r WHERE r.doctor.id = :doctorId AND r.isDeleted = false")
    List<Review> findByDoctor_Id(Long doctorId);
    @Query("SELECT r FROM Review r WHERE r.medicine.id = :medicineId AND r.isDeleted = false")
    List<Review> findByMedicine_Id(Long medicineId);
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.medicine.id = :medicineId AND r.isDeleted = false")
    List<Review> findByMedicine_IdAndUser_Id(Long medicineId, Long userId);
}