package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.ServiceBookingDTO;
import hunre.edu.vn.backend.entity.BookingStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceBookingService {
    List<ServiceBookingDTO.GetServiceBookingDTO> findAll();
    Optional<ServiceBookingDTO.GetServiceBookingDTO> findById(Long id);
    ServiceBookingDTO.GetServiceBookingDTO saveOrUpdate(Map<String, Object> bookingData);
    String deleteByList(List<Long> ids);
    List<ServiceBookingDTO.GetServiceBookingDTO> findByServiceId(Long serviceId);
    List<ServiceBookingDTO.GetServiceBookingDTO> findByPatientId(Long patientId);
    List<ServiceBookingDTO.GetServiceBookingDTO> findByStatus(BookingStatus status);
    List<ServiceBookingDTO.DetailedServiceBookingDto> findByDoctorProfileIdWithDetails(Long doctorProfileId);
    List<ServiceBookingDTO.DetailedServiceBookingDto> findAllWithDetails();
    Optional<ServiceBookingDTO.DetailedServiceBookingDto> findByIdWithDetails(Long id);
    List<ServiceBookingDTO.DetailedServiceBookingDto> findByServiceIdWithDetails(Long serviceId);
    List<ServiceBookingDTO.DetailedServiceBookingDto> findByPatientIdWithDetails(Long patientId);
    List<ServiceBookingDTO.DetailedServiceBookingDto> findByStatusWithDetails(BookingStatus status);
    String cancelServiceBooking(Long id);
}