package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.ServiceBookingDTO;
import hunre.edu.vn.backend.entity.*;
import hunre.edu.vn.backend.mapper.ServiceBookingMapper;
import hunre.edu.vn.backend.repository.*;
import hunre.edu.vn.backend.service.EmailService;
import hunre.edu.vn.backend.service.ServiceBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServiceBookingServiceImpl implements ServiceBookingService {
    private final ServiceBookingMapper serviceBookingMapper;
    private final ServiceBookingRepository serviceBookingRepository;
    private final ServiceRepository serviceRepository;
    private final PatientProfileRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final ConsultationRepository consultationRepository;

    @Autowired
    private EmailService emailService;

    public ServiceBookingServiceImpl(
            ServiceBookingRepository serviceBookingRepository,
            ServiceRepository serviceRepository,
            PatientProfileRepository patientRepository,
            ServiceBookingMapper serviceBookingMapper,
            AppointmentRepository appointmentRepository,
            DoctorProfileRepository doctorProfileRepository,
            ConsultationRepository consultationRepository) {
        this.serviceBookingRepository = serviceBookingRepository;
        this.serviceRepository = serviceRepository;
        this.patientRepository = patientRepository;
        this.serviceBookingMapper = serviceBookingMapper;
        this.appointmentRepository = appointmentRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.consultationRepository = consultationRepository;
    }

    @Override
    public List<ServiceBookingDTO.DetailedServiceBookingDto> findAllWithDetails() {
        return serviceBookingRepository.findAllActive()
                .stream()
                .map(serviceBookingMapper::toDetailedServiceBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceBookingDTO.DetailedServiceBookingDto> findByDoctorProfileIdWithDetails(Long doctorProfileId) {
        return serviceBookingRepository.findByDoctorId(doctorProfileId)
                .stream()
                .map(serviceBookingMapper::toDetailedServiceBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ServiceBookingDTO.DetailedServiceBookingDto> findByIdWithDetails(Long id) {
        return serviceBookingRepository.findActiveById(id)
                .map(serviceBookingMapper::toDetailedServiceBookingDto);
    }

    @Override
    public List<ServiceBookingDTO.DetailedServiceBookingDto> findByServiceIdWithDetails(Long serviceId) {
        return serviceBookingRepository.findByServiceId(serviceId)
                .stream()
                .map(serviceBookingMapper::toDetailedServiceBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceBookingDTO.DetailedServiceBookingDto> findByPatientIdWithDetails(Long patientId) {
        return serviceBookingRepository.findByPatientId(patientId)
                .stream()
                .map(serviceBookingMapper::toDetailedServiceBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceBookingDTO.DetailedServiceBookingDto> findByStatusWithDetails(BookingStatus status) {
        return serviceBookingRepository.findByStatusAndIsDeletedFalse(status)
                .stream()
                .map(serviceBookingMapper::toDetailedServiceBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceBookingDTO.GetServiceBookingDTO> findAll() {
        return serviceBookingRepository.findAllActive()
                .stream()
                .map(serviceBookingMapper::toGetServiceBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ServiceBookingDTO.GetServiceBookingDTO> findById(Long id) {
        return serviceBookingRepository.findActiveById(id)
                .map(serviceBookingMapper::toGetServiceBookingDTO);
    }

    @Override
    @Transactional
    public ServiceBookingDTO.GetServiceBookingDTO saveOrUpdate(Map<String, Object> bookingData) {
        Long id = extractLongValue("id", bookingData);
        Long serviceId = extractLongValue("serviceId", bookingData);
        Long patientId = extractLongValue("patientId", bookingData);
        Long doctorId = extractLongValue("doctorId", bookingData);
        BigDecimal totalPrice = extractBigDecimalValue("totalPrice", bookingData);
        String paymentMethod = extractStringValue("paymentMethod", bookingData);
        String status = extractStringValue("status", bookingData);
        LocalDate appointmentDate = extractLocalDateValue("appointmentDate", bookingData);
        LocalTime appointmentTime = extractLocalTimeValue("appointmentTime", bookingData);

        // Validate required fields
        validateRequiredFields(serviceId, patientId, doctorId, totalPrice);

        ServiceBooking serviceBooking = createOrUpdateServiceBooking(id, serviceId, patientId, doctorId, totalPrice, paymentMethod, status);

        if (id == null || id == 0) {
            createRelatedRecords(serviceBooking, patientId, doctorId, appointmentDate, appointmentTime);
        }

        try {
            User user = serviceBooking.getPatient().getUser();
            emailService.sendServiceBookingConfirmationEmail(user, serviceBooking);
        } catch (Exception e) {
            System.out.println("Lỗi khi gửi email xác nhận dịch vụ: " + e);
        }

        return serviceBookingMapper.toGetServiceBookingDTO(serviceBooking);
    }

    

    @Override
    public List<ServiceBookingDTO.GetServiceBookingDTO> findByServiceId(Long serviceId) {
        return serviceBookingRepository.findByServiceId(serviceId)
                .stream()
                .map(serviceBookingMapper::toGetServiceBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceBookingDTO.GetServiceBookingDTO> findByPatientId(Long patientId) {
        return serviceBookingRepository.findByPatientId(patientId)
                .stream()
                .map(serviceBookingMapper::toGetServiceBookingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceBookingDTO.GetServiceBookingDTO> findByStatus(BookingStatus status) {
        return serviceBookingRepository.findByStatusAndIsDeletedFalse(status)
                .stream()
                .map(serviceBookingMapper::toGetServiceBookingDTO)
                .collect(Collectors.toList());
    }

    private void validateRequiredFields(Long serviceId, Long patientId, Long doctorId, BigDecimal totalPrice) {
        List<String> missingFields = new ArrayList<>();
        if (serviceId == null) missingFields.add("serviceId");
        if (patientId == null) missingFields.add("patientId");
        if (doctorId == null) missingFields.add("doctorId");
        if (totalPrice == null) missingFields.add("totalPrice");

        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException("Missing required fields: " + String.join(", ", missingFields));
        }
    }

    private ServiceBooking createOrUpdateServiceBooking(
            Long id,
            Long serviceId,
            Long patientId,
            Long doctorId,
            BigDecimal totalPrice,
            String paymentMethod,
            String status
    ) {
        ServiceBooking serviceBooking;
        boolean isNewBooking = (id == null || id == 0);

        if (isNewBooking) {
            serviceBooking = new ServiceBooking();
            serviceBooking.setCreatedAt(LocalDateTime.now());
            serviceBooking.setUpdatedAt(LocalDateTime.now());
        } else {
            serviceBooking = serviceBookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Service booking not found with ID: " + id));
            serviceBooking.setUpdatedAt(LocalDateTime.now());
        }

        // Fetch and set relationships
        hunre.edu.vn.backend.entity.Service service = serviceRepository.findActiveById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));
        serviceBooking.setService(service);

        PatientProfile patient = patientRepository.findActiveById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        serviceBooking.setPatient(patient);

        DoctorProfile doctor = doctorProfileRepository.findActiveById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));
        serviceBooking.setDoctor(doctor);
        serviceBooking.setTotalPrice(totalPrice);
        serviceBooking.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        serviceBooking.setStatus(BookingStatus.valueOf(status != null ? status : "PENDING"));

        return serviceBookingRepository.save(serviceBooking);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (serviceBookingRepository.existsById(id)){
                serviceBookingRepository.softDelete(id);
            }
        }

        return "Đã xóa " + ids.size() + " bảng đặt dịch vụ";
    }

    private void createRelatedRecords(
            ServiceBooking savedBooking,
            Long patientId,
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    ) {
        // Kiểm tra các tham số đầu vào
        if (appointmentDate == null || appointmentTime == null) {
            throw new IllegalArgumentException("Appointment date and time must not be null");
        }

        // Create Appointment
        Appointment appointment = new Appointment();
        appointment.setServiceBooking(savedBooking); // Liên kết ServiceBooking
        appointment.setDoctor(doctorProfileRepository.findActiveById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId)));
        appointment.setPatient(patientRepository.findActiveById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId)));
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // Cập nhật ServiceBooking với Appointment
        savedBooking.setAppointment(savedAppointment);
        serviceBookingRepository.save(savedBooking);

        // Create Consultation
        Consultation consultation = new Consultation();
        consultation.setAppointment(savedAppointment);
        consultation.setDoctor(doctorProfileRepository.findActiveById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId)));
        consultation.setPatient(patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId)));

        String consultationCode = generateUniqueConsultationCode();
        consultation.setConsultationCode(consultationCode);
        consultation.setConsultationLink("http://localhost:5173/consultation/call/" + consultationCode);
        consultation.setStatus(ConsultationStatus.PENDING);
        consultationRepository.save(consultation);
    }

    private Long extractLongValue(String key, Map<String, Object> bookingData) {
        Object value = bookingData.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private BigDecimal extractBigDecimalValue(String key, Map<String, Object> bookingData) {
        Object value = bookingData.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String extractStringValue(String key, Map<String, Object> bookingData) {
        Object value = bookingData.get(key);
        return value != null ? value.toString() : null;
    }

    private LocalDate extractLocalDateValue(String key, Map<String, Object> bookingData) {
        Object value = bookingData.get(key);
        if (value == null) return null;
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof String) {
            try {
                return LocalDate.parse((String) value);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }

    private LocalTime extractLocalTimeValue(String key, Map<String, Object> bookingData) {
        Object value = bookingData.get(key);
        if (value == null) return null;
        if (value instanceof LocalTime) return (LocalTime) value;
        if (value instanceof String) {
            try {
                return LocalTime.parse((String) value);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
        return null;
    }

    private String generateUniqueConsultationCode() {
        int maxAttempts = 10; // Giới hạn số lần thử
        int attempts = 0;

        while (attempts < maxAttempts) {
            String code = UUID.randomUUID().toString().substring(0, 8)
                    .replace("-", "")
                    .toUpperCase();

            try {
                // Sử dụng Optional để xử lý an toàn hơn
                boolean exists = Optional.ofNullable(consultationRepository.existsByConsultationCode(code))
                        .orElse(false);

                if (!exists) {
                    return code;
                }
            } catch (Exception e) {
                System.out.println("Lỗi khi kiểm tra mã tư vấn: {}" + e.getMessage());
            }

            attempts++;
        }

        // Nếu không thể tạo mã duy nhất sau nhiều lần thử
        throw new RuntimeException("Không thể tạo mã tư vấn duy nhất sau " + maxAttempts + " lần thử");
    }

    @Override
    @Transactional
    public String cancelServiceBooking(Long id) {
        Optional<ServiceBooking> optionalBooking = serviceBookingRepository.findActiveById(id);

        if (optionalBooking.isEmpty()) {
            return "Không tìm thấy lịch đặt hợp lệ để hủy.";
        }

        ServiceBooking booking = optionalBooking.get();

        // Đổi trạng thái
        booking.setStatus(BookingStatus.CANCELLED);
        serviceBookingRepository.save(booking);

        // Hoàn tiền
        PatientProfile patient = booking.getPatient();
        patient.setAccountBalance(
                patient.getAccountBalance().add(booking.getTotalPrice())
        );
        patientRepository.save(patient);

        return "Đã hủy đặt lịch thành công và hoàn tiền.";
    }

}