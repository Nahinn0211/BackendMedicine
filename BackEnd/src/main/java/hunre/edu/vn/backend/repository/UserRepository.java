package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {
    @Query("SELECT u FROM User as u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User as u WHERE u.fullName = :fullName AND u.isDeleted = false")
    List<User> findByFullNameContaining(String fullName);
    @Query("SELECT u FROM User as u WHERE u.phone = :phone AND u.isDeleted = false")
    List<User> findByPhone(String phone);
    @Query("SELECT u FROM User as u WHERE u.address = :address AND u.isDeleted = false")
    List<User> findByAddressContaining(String address);
    @Query("SELECT u FROM User as u WHERE u.enabled = true AND u.isDeleted = false")
    List<User> findByEnabled(Boolean enabled);
    @Query("SELECT u FROM User as u WHERE u.enabled = false AND u.isDeleted = false")
    List<User> findByLocked(Boolean locked);
    @Query("SELECT u FROM User as u WHERE u.resetPasswordToken = :token AND u.isDeleted = false")
    Optional<User> findByResetPasswordToken(String token);
}