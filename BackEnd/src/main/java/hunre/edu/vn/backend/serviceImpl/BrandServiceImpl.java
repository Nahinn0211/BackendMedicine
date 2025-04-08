package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.BrandDTO;
import hunre.edu.vn.backend.entity.Brand;
import hunre.edu.vn.backend.mapper.BrandMapper;
import hunre.edu.vn.backend.repository.BrandRepository;
import hunre.edu.vn.backend.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BrandServiceImpl implements BrandService {
    private final S3Service s3Service;
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper, S3Service s3Service) {
        this.s3Service = s3Service;
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public List<BrandDTO.GetBrandDTO> findAll() {
        return brandRepository.findAllActive().stream()
                .map(brandMapper::toGetBrandDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BrandDTO.GetBrandDTO> findById(Long id) {
        return brandRepository.findActiveById(id)
                .map(brandMapper::toGetBrandDTO);
    }

    @Override
    public BrandDTO.GetBrandDTO saveOrUpdate(BrandDTO.SaveBrandDTO brandDTO) {
        Brand brand;

        if (brandDTO.getId() == null || brandDTO.getId() == 0) {
            brand = brandMapper.toEntity(brandDTO);
            brand.setCreatedAt(LocalDateTime.now());
            brand.setUpdatedAt(LocalDateTime.now());
        } else {
            Optional<Brand> existingBrand = brandRepository.findActiveById(brandDTO.getId());
            if (existingBrand.isEmpty()) {
                throw new RuntimeException("Brand not found with ID: " + brandDTO.getId());
            }
            brand = existingBrand.get();
            brand.setName(brandDTO.getName());
            brand.setImage(brandDTO.getImage());
            brand.setUpdatedAt(LocalDateTime.now());
        }

        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toGetBrandDTO(savedBrand);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (brandRepository.existsById(id)) {
                brandRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " + ids.size() + " thương hiệu";
    }

    @Override
    public Optional<BrandDTO.GetBrandDTO> findByName(String name) {
        return brandRepository.findByNameAndIsDeletedFalse(name)
                .map(brandMapper::toGetBrandDTO);
    }

    @Override
    public String uploadBrandImage(MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }

    @Override
    public String deleteBrandImage(String image) {
        if (image != null && !image.isEmpty()) {
            try {
                s3Service.deleteFile(image);
                return "Ảnh đã được xóa";
            } catch (Exception e) {
                return "Không thể xóa ảnh" + e.getMessage();
            }
        }
        return "Có lỗi trong khi xóa ảnh";
    }
}
