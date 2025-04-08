package hunre.edu.vn.backend.repository;

import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.DoctorService;
import hunre.edu.vn.backend.entity.Service;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorServiceRepository extends BaseRepository<DoctorService> {
    @Query("SELECT ds FROM DoctorService as ds WHERE ds.doctor.id = :doctorId AND ds.isDeleted = false")
    List<DoctorService> findByDoctor_Id(Long doctorId);
    @Query("SELECT ds FROM DoctorService as ds WHERE ds.service.id = :serviceId AND ds.isDeleted = false")
    List<DoctorService> findByService_Id(Long serviceId);
//    @Modifying
//    void deleteByService_Id(Long serviceId);
//    @Modifying
//    void deleteByDoctor_Id(Long doctorId);
//    @Query("SELECT ds.doctor.id FROM DoctorService ds WHERE ds.service.id = :serviceId")
//    List<Long> findDoctorIdsByServiceId(@Param("serviceId") Long serviceId);
//    @Query("SELECT ds.service.id FROM DoctorService ds WHERE ds.doctor.id = :doctorId")
//    List<Long> findServiceIdsByDoctorId(@Param("doctorId") Long doctorId);
}