package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends BaseRepository<Service> {

    Optional<Service> findByNameAndIsDeletedFalse(String name);
}
