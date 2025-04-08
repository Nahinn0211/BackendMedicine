package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Appointment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends BaseRepository<Appointment> {
    @Query("SELECT a FROM Appointment a WHERE a.isDeleted = false AND a.serviceBooking.id = :serviceBookingId")
    Optional<Appointment> findByServiceBookingId(@Param("serviceBookingId") Long serviceBookingId);

    @Query("SELECT a FROM Appointment a WHERE a.isDeleted = false AND a.doctor.id = :doctorId")
    List<Appointment> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.isDeleted = false AND a.patient.id = :patientId")
    List<Appointment> findByPatientId(@Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.isDeleted = false AND a.appointmentDate = :date")
    List<Appointment> findByAppointmentDate(@Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.isDeleted = false AND a.appointmentDate BETWEEN :startDate AND :endDate")
    List<Appointment> findByAppointmentDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date AND a.isDeleted = false")
    Long countByDate(@Param("date") LocalDate date);

    @Query("SELECT a.appointmentTime FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :appointmentDate AND a.isDeleted = false")
    List<LocalTime> findBookedTimeSlots(
            @Param("doctorId") Long doctorId,
            @Param("appointmentDate") LocalDate date
    );
    // Thống kê lịch hẹn theo tuần
    @Query("SELECT a.appointmentDate, COUNT(a) FROM Appointment a " +
            "WHERE a.appointmentDate BETWEEN :startDate AND :endDate AND a.isDeleted = false " +
            "GROUP BY a.appointmentDate ORDER BY a.appointmentDate")
    List<Object[]> countByWeek(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query("SELECT a FROM Appointment a WHERE a.isDeleted = false AND a.appointmentDate = :appointmentDate AND a.doctor.id = :doctorId")
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

    // Thống kê lịch hẹn theo tháng
    @Query("SELECT FUNCTION('MONTH', a.appointmentDate), COUNT(a) FROM Appointment a " +
            "WHERE FUNCTION('YEAR', a.appointmentDate) = :year AND a.isDeleted = false " +
            "GROUP BY FUNCTION('MONTH', a.appointmentDate) ORDER BY FUNCTION('MONTH', a.appointmentDate)")
    List<Object[]> countByMonth(@Param("year") int year);

    @Query(value = "SELECT " +
            "a.id AS id, " +
            "a.patient_id AS patientId, " +
            "a.doctor_id AS doctorId, " +
            "COALESCE(p.full_name, 'Không xác định') AS patientName, " +
            "COALESCE(d_user.full_name, 'Không xác định') AS doctorName, " +
            "COALESCE(p.email, 'N/A') AS patientEmail, " +
            "COALESCE(d_user.email, 'N/A') AS doctorEmail, " +
            "COALESCE(p.phone, 'N/A') AS patientPhone, " +
            "COALESCE(d_user.phone, 'N/A') AS doctorPhone, " +
            "a.appointment_time AS appointmentTime, " +
            "a.appointment_date AS appointmentDate, " +
            "a.created_at AS createdAt, " +
            "a.updated_at AS updatedAt " +
            "FROM appointments a " +
            "LEFT JOIN users p ON a.patient_id = p.id " +
            "LEFT JOIN doctor_profiles d ON a.doctor_id = d.id " +
            "LEFT JOIN users d_user ON d.user_id = d_user.id " +
            "WHERE a.is_deleted = false AND a.doctor_id = :doctorId",
            nativeQuery = true)
    List<Object[]> findDetailedAppointmentsByDoctorId(@Param("doctorId") Long doctorId);
}