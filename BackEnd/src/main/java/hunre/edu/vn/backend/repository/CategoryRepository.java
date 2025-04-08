package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends BaseRepository<Category> {

    Optional<Category> findByNameAndIsDeletedFalse(String name);
}