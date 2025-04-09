package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.DoctorServiceDTO;
import hunre.edu.vn.backend.dto.ServiceDTO;
import hunre.edu.vn.backend.entity.DoctorService;
import hunre.edu.vn.backend.entity.Service;
import hunre.edu.vn.backend.mapper.ServiceMapper;
import hunre.edu.vn.backend.repository.DoctorServiceRepository;
import hunre.edu.vn.backend.repository.ServiceRepository;
import hunre.edu.vn.backend.service.DoctorServiceService;
import hunre.edu.vn.backend.service.ServiceService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {
    private final ServiceMapper serviceMapper;
    private final ServiceRepository serviceRepository;
    private final S3Service s3Service;
    private final DoctorServiceRepository doctorServiceRepository;
    private final DoctorServiceService doctorServiceService;

    public ServiceServiceImpl(ServiceRepository serviceRepository, DoctorServiceService doctorServiceService, ServiceMapper serviceMapper, S3Service s3Service, DoctorServiceRepository doctorServiceRepository) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.s3Service = s3Service;
        this.doctorServiceRepository = doctorServiceRepository;
        this.doctorServiceService = doctorServiceService;
    }

    @Override
    @Transactional
    public Map<String, Object> saveOrUpdateServiceWithDoctors(ServiceDTO.ServiceWithDoctorsDTO serviceWithDoctorsDTO, MultipartFile file) throws Exception {
        // Chuyển đổi từ ServiceWithDoctorsDTO sang ServiceDTO.SaveServiceDTO
        ServiceDTO.SaveServiceDTO serviceDTO = new ServiceDTO.SaveServiceDTO();
        serviceDTO.setId(serviceWithDoctorsDTO.getId());
        serviceDTO.setName(serviceWithDoctorsDTO.getName());
        serviceDTO.setPrice(serviceWithDoctorsDTO.getPrice());
        serviceDTO.setDescription(serviceWithDoctorsDTO.getDescription());

        // Xử lý hình ảnh nếu có
        if (serviceWithDoctorsDTO.getId() != null) {
            // Trường hợp cập nhật
            Optional<ServiceDTO.GetServiceDTO> existingServiceOpt = findById(serviceWithDoctorsDTO.getId());
            if (existingServiceOpt.isPresent()) {
                ServiceDTO.GetServiceDTO existingService = existingServiceOpt.get();
                if (file != null && !file.isEmpty()) {
                    deleteServiceImage(existingService.getImage());
                    String newImageUrl = uploadServiceImage(file);
                    serviceDTO.setImage(newImageUrl);
                } else {
                    serviceDTO.setImage(existingService.getImage());
                }
                serviceDTO.setUpdatedAt(LocalDateTime.now());
            } else {
                throw new RuntimeException("Không tìm thấy dịch vụ với ID: " + serviceWithDoctorsDTO.getId());
            }
        } else {
            // Trường hợp thêm mới
            if (file != null && !file.isEmpty()) {
                String newImageUrl = uploadServiceImage(file);
                serviceDTO.setImage(newImageUrl);
            }
            serviceDTO.setCreatedAt(LocalDateTime.now());
            serviceDTO.setUpdatedAt(LocalDateTime.now());
        }

        // Lưu hoặc cập nhật dịch vụ
        ServiceDTO.GetServiceDTO savedService = saveOrUpdate(serviceDTO);

        // Xử lý liên kết với bác sĩ
        List<DoctorServiceDTO.GetDoctorServiceDTO> savedDoctorServices = new ArrayList<>();

        if (serviceWithDoctorsDTO.getId() != null) {
            // Nếu là cập nhật, lấy tất cả liên kết hiện tại (cả đang active và đã soft delete)
            List<DoctorService> allExistingLinks = doctorServiceRepository.findByService_IdIncludingDeleted(serviceWithDoctorsDTO.getId());

            // Danh sách ID bác sĩ muốn liên kết
            Set<Long> desiredDoctorIds = new HashSet<>(serviceWithDoctorsDTO.getDoctorIds());

            // Danh sách ID bác sĩ đã liên kết (cả active và deleted)
            Map<Long, DoctorService> existingDoctorMap = allExistingLinks.stream()
                    .collect(Collectors.toMap(
                            ds -> ds.getDoctor().getId(),
                            ds -> ds,
                            (existing, replacement) -> existing
                    ));

            // 1. Khôi phục các liên kết đã bị soft delete nếu cần
            for (Long doctorId : desiredDoctorIds) {
                DoctorService existingLink = existingDoctorMap.get(doctorId);

                if (existingLink != null) {
                    // Nếu liên kết đã tồn tại
                    if (existingLink.getIsDeleted()) {
                        // Khôi phục nếu đã bị soft delete
                        existingLink.setIsDeleted(false);
                        existingLink.setDeletedAt(null);
                        existingLink.setUpdatedAt(LocalDateTime.now());

                        DoctorService restored = doctorServiceRepository.save(existingLink);
                        savedDoctorServices.add(doctorServiceService.convertToDTO(restored));
                    } else {
                        // Nếu đang active, giữ nguyên
                        savedDoctorServices.add(doctorServiceService.convertToDTO(existingLink));
                    }
                } else {
                    // Tạo mới nếu chưa tồn tại
                    DoctorServiceDTO.SaveDoctorServiceDTO newLink = new DoctorServiceDTO.SaveDoctorServiceDTO();
                    newLink.setServiceId(savedService.getId());
                    newLink.setDoctorId(doctorId);

                    DoctorServiceDTO.GetDoctorServiceDTO saved = doctorServiceService.save(newLink);
                    savedDoctorServices.add(saved);
                }
            }

            // 2. Soft delete các liên kết không còn trong danh sách mong muốn
            List<Long> linksToDelete = existingDoctorMap.entrySet().stream()
                    .filter(entry -> !desiredDoctorIds.contains(entry.getKey()))
                    .filter(entry -> !entry.getValue().getIsDeleted())  // Chỉ xóa những cái chưa bị xóa
                    .map(entry -> entry.getValue().getId())
                    .collect(Collectors.toList());

            if (!linksToDelete.isEmpty()) {
                doctorServiceService.deleteByList(linksToDelete);
            }
        } else {
            // Trường hợp thêm mới dịch vụ
            if (serviceWithDoctorsDTO.getDoctorIds() != null && !serviceWithDoctorsDTO.getDoctorIds().isEmpty()) {
                for (Long doctorId : serviceWithDoctorsDTO.getDoctorIds()) {
                    DoctorServiceDTO.SaveDoctorServiceDTO doctorServiceDTO = new DoctorServiceDTO.SaveDoctorServiceDTO();
                    doctorServiceDTO.setServiceId(savedService.getId());
                    doctorServiceDTO.setDoctorId(doctorId);

                    DoctorServiceDTO.GetDoctorServiceDTO savedDoctorService =
                            doctorServiceService.save(doctorServiceDTO);

                    savedDoctorServices.add(savedDoctorService);
                }
            }
        }

        // Trả về kết quả trong Map
        Map<String, Object> result = new HashMap<>();
        result.put("service", savedService);
        result.put("doctorServices", savedDoctorServices);

        return result;
    }

    @Override
    public List<Long> getDoctorIdsByServiceId(Long serviceId) {
        List<DoctorServiceDTO.GetDoctorServiceDTO> doctorServices = doctorServiceService.findByServiceId(serviceId);
        return doctorServices.stream()
                .map(DoctorServiceDTO.GetDoctorServiceDTO::getDoctorId)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDTO.GetServiceDTO> findAll() {
        return serviceRepository.findAllActive()
                .stream()
                .map(serviceMapper::toGetServiceDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ServiceDTO.GetServiceDTO> findById(Long id) {
        return serviceRepository.findActiveById(id)
                .map(serviceMapper::toGetServiceDTO);
    }

    @Override
    @Transactional
    public ServiceDTO.GetServiceDTO saveOrUpdate(ServiceDTO.SaveServiceDTO serviceDTO) {
        Service service;

        if (serviceDTO.getId() == null || serviceDTO.getId() == 0) {
            service = new Service();
            service.setCreatedAt(LocalDateTime.now());
            service.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Service> existingService = serviceRepository.findActiveById(serviceDTO.getId());
            if (existingService.isEmpty()) {
                throw new RuntimeException("Service not found with ID: " + serviceDTO.getId());
            }
            service = existingService.get();
            service.setUpdatedAt(LocalDateTime.now());
        }

        // Cập nhật các trường
        service.setName(serviceDTO.getName());
        service.setImage(serviceDTO.getImage());
        service.setPrice(serviceDTO.getPrice());
        service.setDescription(serviceDTO.getDescription());

        Service savedService = serviceRepository.save(service);
        return serviceMapper.toGetServiceDTO(savedService);
    }

    @Override
    public String uploadServiceImage(MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }

    @Override
    public String deleteServiceImage(String image) {
        if (image != null && !image.isEmpty()) {
            try {
                s3Service.deleteFile(image);
                return "Ảnh đã được xóa";
            } catch (Exception e) {
                return "Lỗi trong quá trình xóa ảnh " + e.getMessage();
            }
        }
        return "Không thể xóa ảnh";
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (serviceRepository.existsById(id)){
                serviceRepository.softDelete(id);
            }
        }

        return "Đã xóa thành công  " + ids.size() + " dịch vụ";
    }

    @Override
    public List<ServiceDTO.GetServiceDTO> findByName(String name) {
        return serviceRepository.findByNameAndIsDeletedFalse(name)
                .stream()
                .map(serviceMapper::toGetServiceDTO)
                .collect(Collectors.toList());
    }
}