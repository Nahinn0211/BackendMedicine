package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceBookingRepository extends BaseRepository<ServiceBooking> {

    List<ServiceBooking> findByPatientAndIsDeletedFalse(PatientProfile patient);

    List<ServiceBooking> findByDoctorAndIsDeletedFalse(DoctorProfile doctor);

    List<ServiceBooking> findByServiceAndIsDeletedFalse(Service service);

    List<ServiceBooking> findByStatusAndIsDeletedFalse(BookingStatus status);

    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.patient.id = ?1 AND sb.isDeleted = false ORDER BY sb.createdAt DESC")
    List<ServiceBooking> findByPatientId(Long patientId);

    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.doctor.id = ?1 AND sb.isDeleted = false ORDER BY sb.createdAt DESC")
    List<ServiceBooking> findByDoctorId(Long doctorId);

    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.service.id = ?1 AND sb.isDeleted = false")
    List<ServiceBooking> findByServiceId(Long serviceId);

    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.createdAt BETWEEN ?1 AND ?2 AND sb.isDeleted = false")
    List<ServiceBooking> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDateTime);
}
