package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Voucher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends BaseRepository<Voucher> {

    Optional<Voucher> findByCodeAndIsDeletedFalse(String code);

    List<Voucher> findByStatusAndIsDeletedFalse(Voucher.VoucherStatus status);

    @Query("SELECT v FROM Voucher v WHERE v.startDate <= ?1 AND (v.endDate IS NULL OR v.endDate >= ?1) AND v.status = 'ACTIVE' AND v.stock > 0 AND v.isDeleted = false")
    List<Voucher> findActiveVouchers(LocalDateTime currentTime);

    @Query("SELECT v FROM Voucher v WHERE v.minimumOrderValue <= ?1 AND v.startDate <= ?2 AND (v.endDate IS NULL OR v.endDate >= ?2) AND v.status = 'ACTIVE' AND v.stock > 0 AND v.isDeleted = false")
    List<Voucher> findApplicableVouchers(BigDecimal orderValue, LocalDateTime currentTime);

    @Query("SELECT v FROM Voucher v WHERE v.endDate BETWEEN ?1 AND ?2 AND v.status = 'ACTIVE' AND v.isDeleted = false")
    List<Voucher> findVouchersExpiringBetween(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByCodeAndIsDeletedFalse(String code);
}