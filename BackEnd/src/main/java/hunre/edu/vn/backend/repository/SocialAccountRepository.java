package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.SocialAccount;
import hunre.edu.vn.backend.entity.SocialProvider;
import hunre.edu.vn.backend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SocialAccountRepository extends BaseRepository<SocialAccount> {
    @Query("SELECT sa FROM SocialAccount sa WHERE sa.user.id = :userId AND sa.isDeleted = false")
    List<SocialAccount> findByUserId(Long userId);
    @Query("SELECT sa FROM SocialAccount sa WHERE sa.providerEmail = :email AND sa.isDeleted = false")
    Optional<SocialAccount> findByProviderEmail(String email);
}
