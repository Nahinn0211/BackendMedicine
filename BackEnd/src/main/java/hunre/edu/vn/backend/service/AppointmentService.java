package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.AppointmentDTO;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface AppointmentService {
    List<hunre.edu.vn.backend.dto.AppointmentDTO.GetAppointmentDTO> findAll();
    Optional<AppointmentDTO.GetAppointmentDTO> findById(Long id);
    AppointmentDTO.GetAppointmentDTO saveOrUpdate(AppointmentDTO.SaveAppointmentDTO appointmentDTO);
    String deleteByList(List<Long> ids);
    Optional<AppointmentDTO.GetAppointmentDTO> findAppointmentByServiceBookingId(Long id);
    List<AppointmentDTO.GetAppointmentDTO> findByDoctorId(Long doctorId);
    List<AppointmentDTO.GetAppointmentDTO> findByPatientId(Long patientId);
    List<AppointmentDTO.GetAppointmentDTO> findByDate(LocalDate date);
    List<AppointmentDTO.GetAppointmentDTO> findByDateRange(LocalDate startDate, LocalDate endDate);
    Long countByDate(LocalDate date);
    Map<LocalDate, Long> getWeeklyStats(LocalDate startDate, LocalDate endDate);
    Map<Integer, Long> getMonthlyStats(int year);
    AppointmentDTO.AppointmentStatsDTO getDashboardStats();
    Boolean checkAvailabilityForDoctor(Long doctorId, LocalDate date, LocalTime time);
    List<String> getDoctorBookedSlots(Long doctorId, LocalDate date);
}