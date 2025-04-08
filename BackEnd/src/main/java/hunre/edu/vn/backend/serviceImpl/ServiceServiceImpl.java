package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.ServiceDTO;
import hunre.edu.vn.backend.entity.Service;
import hunre.edu.vn.backend.mapper.ServiceMapper;
import hunre.edu.vn.backend.repository.DoctorServiceRepository;
import hunre.edu.vn.backend.repository.ServiceRepository;
import hunre.edu.vn.backend.service.ServiceService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {
    private final ServiceMapper serviceMapper;
    private final ServiceRepository serviceRepository;
    private final S3Service s3Service;
    private final DoctorServiceRepository doctorServiceRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository, ServiceMapper serviceMapper, S3Service s3Service, DoctorServiceRepository doctorServiceRepository) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.s3Service = s3Service;
        this.doctorServiceRepository = doctorServiceRepository;
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
            System.out.println(existingService);
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