package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.MedicineMediaDTO;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.MedicineMedia;
import hunre.edu.vn.backend.mapper.MedicineMediaMapper;
import hunre.edu.vn.backend.repository.MedicineMediaRepository;
import hunre.edu.vn.backend.repository.MedicineRepository;
import hunre.edu.vn.backend.service.MedicineMediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicineMediaServiceImpl implements MedicineMediaService {

    private final MedicineMediaRepository medicineMediaRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineMediaMapper medicineMediaMapper;
    private final S3Service s3Service;

    @Autowired
    public MedicineMediaServiceImpl(
            MedicineMediaRepository medicineMediaRepository,
            MedicineRepository medicineRepository,
            MedicineMediaMapper medicineMediaMapper, S3Service s3Service) {
        this.medicineMediaRepository = medicineMediaRepository;
        this.medicineRepository = medicineRepository;
        this.s3Service = s3Service;
        this.medicineMediaMapper = medicineMediaMapper;
    }

    @Override
    public List<MedicineMediaDTO.GetMedicineMediaDTO> findAll() {
        return medicineMediaRepository.findAllActive().stream()
                .map(medicineMediaMapper::toGetMedicineMediaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MedicineMediaDTO.GetMedicineMediaDTO> findById(Long id) {
        return medicineMediaRepository.findActiveById(id)
                .map(medicineMediaMapper::toGetMedicineMediaDTO);
    }

    @Override
    @Transactional
    public MedicineMediaDTO.GetMedicineMediaDTO saveOrUpdate(MedicineMediaDTO.SaveMedicineMediaDTO medicineMediaDTO) {
        MedicineMedia medicineMedia;

        if (medicineMediaDTO.getId() == null || medicineMediaDTO.getId() == 0) {
            // INSERT case
            medicineMedia = new MedicineMedia();
            medicineMedia.setCreatedAt(LocalDateTime.now());
            medicineMedia.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<MedicineMedia> existingMedia = medicineMediaRepository.findActiveById(medicineMediaDTO.getId());
            if (existingMedia.isEmpty()) {
                throw new RuntimeException("Medicine media not found with ID: " + medicineMediaDTO.getId());
            }
            medicineMedia = existingMedia.get();
            medicineMedia.setUpdatedAt(LocalDateTime.now());
        }

        // Xử lý medicine relationship
        Medicine medicine = medicineRepository.findActiveById(medicineMediaDTO.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + medicineMediaDTO.getMedicineId()));
        medicineMedia.setMedicine(medicine);

        medicineMedia.setMediaUrl(medicineMediaDTO.getMediaUrl());
        medicineMedia.setMainImage(medicineMediaDTO.getMainImage());

        // Nếu đánh dấu là ảnh chính, hãy tắt các ảnh chính khác của cùng thuốc
        if (Boolean.TRUE.equals(medicineMediaDTO.getMainImage())) {
            medicineMediaRepository.findMainImageByMedicineId(medicineMediaDTO.getMedicineId())
                    .ifPresent(existingMainImage -> {
                        if (!existingMainImage.getId().equals(medicineMediaDTO.getId())) {
                            existingMainImage.setMainImage(false);
                            medicineMediaRepository.save(existingMainImage);
                        }
                    });
        }

        MedicineMedia savedMedia = medicineMediaRepository.save(medicineMedia);
        return medicineMediaMapper.toGetMedicineMediaDTO(savedMedia);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (medicineMediaRepository.findActiveById(id).isPresent()) {
                medicineMediaRepository.deleteById(id);
            }
        }

        return "Đã xóa thành công " + ids.size() + " ảnh của thuốc";
    }

    @Override
    public List<MedicineMediaDTO.GetMedicineMediaDTO> findByMedicineId(Long medicineId) {
        return medicineMediaRepository.findByMedicineId(medicineId).stream()
                .map(medicineMediaMapper::toGetMedicineMediaDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String uploadMedicineImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        try {
            return s3Service.uploadFile(file);
        } catch (IOException e) {
            throw new IOException("Không thể tải lên hình ảnh thuốc: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public String deleteAllMediaByMedicineId(Long medicineId) {
        if (!medicineRepository.existsById(medicineId)) {
            throw new RuntimeException("Không tìm thấy thuốc với ID: " + medicineId);
        }

        List<MedicineMedia> medicineMediaList = medicineMediaRepository.findByMedicineId(medicineId);

        if (medicineMediaList.isEmpty()) {
            return "Không tìm thấy ảnh của thuốc";
        }

        for (MedicineMedia medicineMedia : medicineMediaList) {
            try {
                s3Service.deleteFile(medicineMedia.getMediaUrl());

                return "Đã xóa thành công ảnh của thuốc";
            } catch (Exception e) {

                return "Lỗi khi xóa media " + medicineMedia.getId() + ": " + e.getMessage();
            }
        }

        medicineMediaRepository.deleteAll(medicineMediaList);
        return "Đã xóa thành công ảnh của thuốc";
    }

    @Override
    @Transactional
    public String updateMedicineImage(Long mediaId, MultipartFile file) throws IOException {
        MedicineMedia existingMedia = medicineMediaRepository.findActiveById(mediaId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy media với ID: " + mediaId));

        try {
            s3Service.deleteFile(existingMedia.getMediaUrl());
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa file cũ: " + e.getMessage());
        }

        return uploadMedicineImage(file);
    }

    @Override
    public Optional<MedicineMediaDTO.GetMedicineMediaDTO> findMainImageByMedicineId(Long medicineId) {
        return medicineMediaRepository.findMainImageByMedicineId(medicineId)
                .map(medicineMediaMapper::toGetMedicineMediaDTO);
    }
}