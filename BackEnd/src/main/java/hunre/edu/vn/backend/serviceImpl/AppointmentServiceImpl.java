package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.AppointmentDTO;
import hunre.edu.vn.backend.entity.Appointment;
import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.PatientProfile;
import hunre.edu.vn.backend.exception.ResourceNotFoundException;
import hunre.edu.vn.backend.mapper.AppointmentMapper;
import hunre.edu.vn.backend.repository.AppointmentRepository;
import hunre.edu.vn.backend.repository.DoctorProfileRepository;
import hunre.edu.vn.backend.repository.PatientProfileRepository;
import hunre.edu.vn.backend.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientProfileRepository patientProfileRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper, PatientProfileRepository patientProfileRepository, DoctorProfileRepository doctorProfileRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.patientProfileRepository = patientProfileRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @Override
    public List<AppointmentDTO.GetAppointmentDTO> findAll() {
        return appointmentRepository.findAllActive()
                .stream()
                .map(appointmentMapper::toGetAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AppointmentDTO.GetAppointmentDTO> findById(Long id) {
        return appointmentRepository.findActiveById(id).
                map(appointmentMapper::toGetAppointmentDTO);
    }

    @Override
    public AppointmentDTO.GetAppointmentDTO saveOrUpdate(AppointmentDTO.SaveAppointmentDTO appointmentDTO) {
        Appointment appointment;

        PatientProfile patientProfile = patientProfileRepository.findActiveById(appointmentDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + appointmentDTO.getPatientId()));
        DoctorProfile doctorProfile = doctorProfileRepository.findActiveById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + appointmentDTO.getDoctorId()));
        if (appointmentDTO.getId() != null) {
            appointment = appointmentRepository.findActiveById(appointmentDTO.getId()).orElse(null);
            appointment.setPatient(patientProfile);
            appointment.setDoctor(doctorProfile);
            appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
            appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
            appointment.setUpdatedAt(LocalDateTime.now());
        }else{
            appointment = new Appointment();
            appointment.setPatient(patientProfile);
            appointment.setDoctor(doctorProfile);
            appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
            appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
            appointment.setUpdatedAt(LocalDateTime.now());
            appointment.setCreatedAt(LocalDateTime.now());
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toGetAppointmentDTO(savedAppointment);
    }
    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (appointmentRepository.findActiveById(id).isPresent()) {
                appointmentRepository.softDelete(id);
            }
        }
        return "Deleted Successfully";
    }

    @Override
    public Optional<AppointmentDTO.GetAppointmentDTO> findAppointmentByServiceBookingId(Long id) {
        return appointmentRepository.findByServiceBookingId(id)
                .map(appointmentMapper::toGetAppointmentDTO);
    }

    @Override
    public List<AppointmentDTO.GetAppointmentDTO> findByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(appointmentMapper::toGetAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO.GetAppointmentDTO> findByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId)
                .stream()
                .map(appointmentMapper::toGetAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO.GetAppointmentDTO> findByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date)
                .stream()
                .map(appointmentMapper::toGetAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO.GetAppointmentDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByAppointmentDateBetween(startDate, endDate)
                .stream()
                .map(appointmentMapper::toGetAppointmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countByDate(LocalDate date) {
        return appointmentRepository.countByDate(date);
    }

    @Override
    public Map<LocalDate, Long> getWeeklyStats(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = appointmentRepository.countByWeek(startDate, endDate);
        Map<LocalDate, Long> stats = new HashMap<>();

        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            Long count = ((Number) result[1]).longValue();
            stats.put(date, count);
        }

        return stats;
    }

    @Override
    public Map<Integer, Long> getMonthlyStats(int year) {
        List<Object[]> results = appointmentRepository.countByMonth(year);
        Map<Integer, Long> stats = new HashMap<>();

        for (Object[] result : results) {
            Integer month = ((Number) result[0]).intValue();
            Long count = ((Number) result[1]).longValue();
            stats.put(month, count);
        }

        return stats;
    }

    @Override
    public AppointmentDTO.AppointmentStatsDTO getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        long todayCount = countByDate(today);
        Map<LocalDate, Long> weeklyStats = getWeeklyStats(startOfWeek, endOfWeek);
        Map<Integer, Long> monthlyStats = getMonthlyStats(today.getYear());

        long weeklyTotal = weeklyStats.values().stream().mapToLong(Long::longValue).sum();
        long monthlyTotal = monthlyStats.values().stream().mapToLong(Long::longValue).sum();

        // Lấy tháng hiện tại
        int currentMonth = today.getMonthValue();
        long currentMonthTotal = monthlyStats.getOrDefault(currentMonth, 0L);

        return AppointmentDTO.AppointmentStatsDTO.builder()
                .todayAppointments(todayCount)
                .weeklyAppointments(weeklyTotal)
                .monthlyAppointments(currentMonthTotal)
                .totalAppointments(monthlyTotal)
                .weeklyStats(weeklyStats)
                .monthlyStats(monthlyStats)
                .build();
    }

    @Override
    public Boolean checkAvailabilityForDoctor(Long doctorId, LocalDate date, LocalTime time) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date);
        return appointments.stream()
                .noneMatch(app -> app.getAppointmentTime().equals(time));
    }

    @Override
    public List<String> getDoctorBookedSlots(Long doctorId, LocalDate date) {
        List<LocalTime> bookedTimeSlots = appointmentRepository.findBookedTimeSlots(doctorId, date);
        return bookedTimeSlots.stream()
                .map(time -> time.format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());
    }
}
