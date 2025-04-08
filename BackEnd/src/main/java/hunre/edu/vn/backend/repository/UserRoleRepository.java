package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Role;
import hunre.edu.vn.backend.entity.User;
import hunre.edu.vn.backend.entity.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends BaseRepository<UserRole> {
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.id = ?1 AND ur.isDeleted = false")
    List<UserRole> findByUserId(Long userId);

    @Query("SELECT ur FROM UserRole ur WHERE ur.role.id = ?1 AND ur.isDeleted = false")
    List<UserRole> findByRoleId(Long roleId);
}