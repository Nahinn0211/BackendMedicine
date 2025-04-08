package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.AppointmentDTO;
import hunre.edu.vn.backend.dto.UserDTO;
import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.repository.AppointmentRepository;
import hunre.edu.vn.backend.repository.DoctorProfileRepository;
import hunre.edu.vn.backend.service.AppointmentService;
import hunre.edu.vn.backend.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;


    public AppointmentController(AppointmentService appointmentService,
                                 UserService userService, DoctorProfileRepository doctorProfileRepository, AppointmentRepository appointmentRepository) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.doctorProfileRepository = doctorProfileRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO.GetAppointmentDTO>> getAllAttributes() {
        List<AppointmentDTO.GetAppointmentDTO> appointments = appointmentService.findAll();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO.GetAppointmentDTO> getAppointmentById(@PathVariable Long id) {
        return appointmentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<AppointmentDTO.GetAppointmentDTO> saveOrUpdateAppointment(@RequestBody AppointmentDTO.SaveAppointmentDTO appointmentDTO) {
        AppointmentDTO.GetAppointmentDTO savedAppointment = appointmentService.saveOrUpdate(appointmentDTO);
        return ResponseEntity.ok(savedAppointment);
    }

    @DeleteMapping("/delete")
    public String deleteAttribute(@RequestBody List<Long> ids) {
        return appointmentService.deleteByList(ids);
    }

    @GetMapping("/service-bookings/{id}")
    public ResponseEntity<AppointmentDTO.GetAppointmentDTO> getAppointmentByServiceBookingId(@PathVariable Long id) {
        return appointmentService.findAppointmentByServiceBookingId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO.GetAppointmentDTO>> getAppointmentsByDoctorId(
            @PathVariable Long doctorId) {
        List<AppointmentDTO.GetAppointmentDTO> appointments = appointmentService.findByDoctorId(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO.GetAppointmentDTO>> getAppointmentsByPatientId(
            @PathVariable Long patientId) {
        List<AppointmentDTO.GetAppointmentDTO> appointments = appointmentService.findByPatientId(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date")
    public ResponseEntity<List<AppointmentDTO.GetAppointmentDTO>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentDTO.GetAppointmentDTO> appointments = appointmentService.findByDate(date);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AppointmentDTO.GetAppointmentDTO>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AppointmentDTO.GetAppointmentDTO> appointments = appointmentService.findByDateRange(startDate, endDate);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/stats/dashboard")
    public ResponseEntity<AppointmentDTO.AppointmentStatsDTO> getDashboardStats() {
        AppointmentDTO.AppointmentStatsDTO stats = appointmentService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/weekly")
    public ResponseEntity<Map<LocalDate, Long>> getWeeklyStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<LocalDate, Long> stats = appointmentService.getWeeklyStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/monthly")
    public ResponseEntity<Map<Integer, Long>> getMonthlyStats(
            @RequestParam int year) {
        Map<Integer, Long> stats = appointmentService.getMonthlyStats(year);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check-availability")
    public Boolean checkAvailability(@RequestParam Long doctorId, @RequestParam LocalDate date, @RequestParam LocalTime timeSlot) {
        return appointmentService.checkAvailabilityForDoctor(doctorId, date, timeSlot);
    }

    @GetMapping("/booked-slots")
    public List<String> getBookedSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return appointmentService.getDoctorBookedSlots(doctorId, date);
    }

    @GetMapping("/doctor/me")
    public ResponseEntity<?> getCurrentDoctorAppointments() {
        // Kiểm tra xác thực
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Chưa đăng nhập"));
        }

        // Lấy email từ Authentication
        String email = authentication.getName();

        // Tìm thông tin người dùng theo email
        Optional<UserDTO.GetUserDTO> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Không tìm thấy thông tin người dùng"));
        }

        // Lấy ID của người dùng hiện tại
        Long currentUserId = userOptional.get().getId();

        // Tìm profile bác sĩ dựa trên user_id
        Optional<DoctorProfile> doctorProfileOptional = doctorProfileRepository.findByUser_Id(currentUserId);

        if (doctorProfileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Không phải là hồ sơ bác sĩ"));
        }

        // Lấy doctorId từ DoctorProfile
        Long doctorId = doctorProfileOptional.get().getId();

        // Lấy danh sách lịch hẹn của bác sĩ
        List<Object[]> appointmentResults = appointmentRepository.findDetailedAppointmentsByDoctorId(doctorId);

        // Chuyển đổi kết quả
        List<AppointmentDTO.AppointmentDetailsDTO> appointments = appointmentResults.stream()
                .map(this::convertToAppointmentDetails)
                .collect(Collectors.toList());

        // Nếu không có lịch hẹn
        if (appointments.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Trả về danh sách lịch hẹn chi tiết
        return ResponseEntity.ok(appointments);
    }

    private AppointmentDTO.AppointmentDetailsDTO convertToAppointmentDetails(Object[] result) {
        return AppointmentDTO.AppointmentDetailsDTO.builder()
                .id(((Number) result[0]).longValue())
                .patientId(((Number) result[1]).longValue())
                .doctorId(((Number) result[2]).longValue())
                .patientName((String) result[3])
                .doctorName((String) result[4])
                .patientEmail((String) result[5])
                .doctorEmail((String) result[6])
                .patientPhone((String) result[7])
                .doctorPhone((String) result[8])
                .appointmentTime(((Time) result[9]).toLocalTime())
                .appointmentDate(((Date) result[10]).toLocalDate())
                .createdAt(((Timestamp) result[11]).toLocalDateTime())
                .updatedAt(((Timestamp) result[12]).toLocalDateTime())
                .build();
    }
}
