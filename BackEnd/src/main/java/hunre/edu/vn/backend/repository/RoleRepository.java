package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role> {

    Optional<Role> findByNameAndIsDeletedFalse(String name);
}