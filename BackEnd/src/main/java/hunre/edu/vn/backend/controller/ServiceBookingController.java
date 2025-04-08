package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.ServiceBookingDTO;
import hunre.edu.vn.backend.dto.UserDTO;
import hunre.edu.vn.backend.entity.BookingStatus;
import hunre.edu.vn.backend.service.DoctorProfileService;
import hunre.edu.vn.backend.service.PatientProfileService;
import hunre.edu.vn.backend.service.ServiceBookingService;
import hunre.edu.vn.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/service-bookings")
public class ServiceBookingController {

    private final ServiceBookingService serviceBookingService;
    private final UserService userService;
    private final DoctorProfileService doctorProfileService;
    private final PatientProfileService patientProfileService;

    public ServiceBookingController(ServiceBookingService serviceBookingService, UserService userService, DoctorProfileService doctorProfileService, PatientProfileService patientProfileService) {
        this.serviceBookingService = serviceBookingService;
        this.userService = userService;
        this.doctorProfileService = doctorProfileService;
        this.patientProfileService = patientProfileService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceBookingDTO.GetServiceBookingDTO>> getAllServiceBookings() {
        List<ServiceBookingDTO.GetServiceBookingDTO> bookings = serviceBookingService.findAll();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceBookingDTO.GetServiceBookingDTO> getServiceBookingById(@PathVariable Long id) {
        return serviceBookingService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<ServiceBookingDTO.GetServiceBookingDTO> saveOrUpdateServiceBooking(@RequestBody Map<String, Object> bookingData) {
        ServiceBookingDTO.GetServiceBookingDTO savedBooking = serviceBookingService.saveOrUpdate(bookingData);
        return ResponseEntity.ok(savedBooking);
    }

    @DeleteMapping("/{id}")
    public String deleteServiceBooking(@RequestBody List<Long> ids) {
        return serviceBookingService.deleteByList(ids);
    }

    @GetMapping("/by-service/{serviceId}")
    public ResponseEntity<List<ServiceBookingDTO.GetServiceBookingDTO>> getServiceBookingsByServiceId(@PathVariable Long serviceId) {
        List<ServiceBookingDTO.GetServiceBookingDTO> bookings = serviceBookingService.findByServiceId(serviceId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<ServiceBookingDTO.GetServiceBookingDTO>> getServiceBookingsByPatientId(@PathVariable Long patientId) {
        List<ServiceBookingDTO.GetServiceBookingDTO> bookings = serviceBookingService.findByPatientId(patientId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<ServiceBookingDTO.GetServiceBookingDTO>> getServiceBookingsByStatus(@PathVariable BookingStatus status) {
        List<ServiceBookingDTO.GetServiceBookingDTO> bookings = serviceBookingService.findByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/getUserServiceBookings")
    public ResponseEntity<?> getUserServiceBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Chưa đăng nhập"));
        }

        // Lấy email từ Authentication
        String email = authentication.getName();

        Optional<UserDTO.GetUserDTO> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Không tìm thấy thông tin người dùng"));
        }

        UserDTO.GetUserDTO currentUser = userOptional.get();

        // Lấy danh sách các vai trò của người dùng
        List<String> userRoles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        boolean isDoctor = userRoles.contains("DOCTOR");
        boolean isPatient = userRoles.contains("PATIENT");

        List<ServiceBookingDTO.DetailedServiceBookingDto> bookings;

        if (isDoctor) {
            Long doctorProfileId = userService.getDoctorProfileIdByUserId(currentUser.getId());
            bookings = serviceBookingService.findByDoctorProfileIdWithDetails(doctorProfileId);
        } else if (isPatient) {
            Long patientProfileId = userService.getPatientProfileIdByUserId(currentUser.getId());
            bookings = serviceBookingService.findByPatientIdWithDetails(patientProfileId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Người dùng không có quyền bác sĩ hoặc bệnh nhân"));
        }

        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/cancel/{id}")
    public String cancelServiceBooking(@PathVariable Long id) {
        return serviceBookingService.cancelServiceBooking(id);
    }

    private List<ServiceBookingDTO.DetailedServiceBookingDto> findDoctorBookings(Long doctorProfileId) {
        throw new UnsupportedOperationException("Chưa triển khai tìm kiếm đặt dịch vụ theo ID bác sĩ");
    }

    private List<ServiceBookingDTO.DetailedServiceBookingDto> findPatientBookings(Long patientProfileId) {
        return serviceBookingService.findByPatientIdWithDetails(patientProfileId);
    }
}