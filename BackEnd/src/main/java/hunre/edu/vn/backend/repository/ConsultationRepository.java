package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.Consultation;
import hunre.edu.vn.backend.entity.ConsultationStatus;
import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.PatientProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends BaseRepository<Consultation> {
    @Query("SELECT c FROM Consultation as c WHERE c.consultationCode = :code AND c.isDeleted = false")
    Boolean existsByConsultationCode(String code);
    @Query("SELECT c FROM Consultation as c WHERE c.patient.id = :patientId AND c.isDeleted = false")
    List<Consultation> findByPatientId(Long patientId);
    @Query("SELECT c FROM Consultation as c WHERE c.appointment.id = :appointmentId AND c.isDeleted = false")
    Optional<Consultation> findByAppointmentId(Long appointmentId);
    @Query("SELECT c FROM Consultation as c WHERE c.consultationCode = :code AND c.isDeleted = false")
    Optional<Consultation> findByConsultationCode(String code);
}
