package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.BrandDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface BrandService {
    List<BrandDTO.GetBrandDTO> findAll();
    Optional<BrandDTO.GetBrandDTO> findById(Long id);
    BrandDTO.GetBrandDTO saveOrUpdate(BrandDTO.SaveBrandDTO brandDTO);
    String deleteByList(List<Long> ids);
    String uploadBrandImage(MultipartFile file) throws IOException;
    String deleteBrandImage(String image);
    Optional<BrandDTO.GetBrandDTO> findByName(String name);
}
