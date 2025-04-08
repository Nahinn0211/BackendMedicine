package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescriptionRepository extends BaseRepository<Prescription> {
    @Query("SELECT p FROM Prescription p WHERE p.isDeleted = false AND p.doctor.id = :doctorId")
    List<Prescription> findByDoctor_Id(Long doctorId);
    @Query("SELECT p FROM Prescription p WHERE p.isDeleted = false AND p.patient.id = :patientId")
    List<Prescription> findByPatient_Id(Long patientId);
    @Query("SELECT p FROM Prescription p WHERE p.isDeleted = false AND p.medicine.id = :medicineId")
    List<Prescription> findByMedicine_Id(Long medicineId);
}