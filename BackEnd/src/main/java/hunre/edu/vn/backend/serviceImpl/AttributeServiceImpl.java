package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.AttributeDTO;
import hunre.edu.vn.backend.entity.Attribute;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.mapper.AttributeMapper;
import hunre.edu.vn.backend.repository.AttributeRepository;
import hunre.edu.vn.backend.repository.MedicineRepository;
import hunre.edu.vn.backend.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final MedicineRepository medicineRepository;
    private final AttributeMapper attributeMapper;

    @Override
    public List<AttributeDTO.GetAttributeDTO> findAll() {
        return attributeRepository.findAllActive().stream()
                .map(attributeMapper::toGetAttributeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AttributeDTO.GetAttributeDTO> findById(Long id) {
        return attributeRepository.findActiveById(id)
                .map(attributeMapper::toGetAttributeDTO);
    }

    @Override
    @Transactional
    public AttributeDTO.GetAttributeDTO saveOrUpdate(AttributeDTO.SaveAttributeDTO attributeDTO) {
        Attribute attribute;

        if (attributeDTO.getId() == null || attributeDTO.getId() == 0) {
            // INSERT case
            attribute = new Attribute();
            attribute.setCreatedAt(LocalDateTime.now());
            attribute.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Attribute> existingAttribute = attributeRepository.findActiveById(attributeDTO.getId());
            if (existingAttribute.isEmpty()) {
                throw new RuntimeException("Attribute not found with ID: " + attributeDTO.getId());
            }
            attribute = existingAttribute.get();
            attribute.setUpdatedAt(LocalDateTime.now());
        }

        if (attributeDTO.getMedicineId() != null) {
            Medicine medicine = medicineRepository.findActiveById(attributeDTO.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + attributeDTO.getMedicineId()));
            attribute.setMedicine(medicine);
        }

        attribute.setName(attributeDTO.getName());
        attribute.setStock(attributeDTO.getStock());
        attribute.setExpiryDate(attributeDTO.getExpiryDate());
        attribute.setPriceIn(attributeDTO.getPriceIn());
        attribute.setPriceOut(attributeDTO.getPriceOut());

        Attribute savedAttribute = attributeRepository.save(attribute);
        return attributeMapper.toGetAttributeDTO(savedAttribute);
    }

    @Override
    public List<AttributeDTO.GetAttributeDTO> findByMedicineId(Long medicineId) {
        // Gọi repository để lấy thuộc tính theo medicineId
        List<Attribute> attributes = attributeRepository.findByMedicineId(medicineId);

        if (attributes.isEmpty()) {
            return Collections.emptyList();
        }

        // Chuyển đổi thành DTO và trả về
        return attributes.stream()
                .map(attributeMapper::toGetAttributeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (attributeRepository.existsById(id)) {
                attributeRepository.softDelete(id);
            }
        }
        return "Đã xóa thành công " + ids.size() + " thuộc tính";
    }

    @Override
    @Transactional
    public List<AttributeDTO.GetAttributeDTO> saveOrUpdateAll(List<AttributeDTO.SaveAttributeDTO> attributeDTOs) {
        if (attributeDTOs == null || attributeDTOs.isEmpty()) {
            return Collections.emptyList();
        }

        List<AttributeDTO.GetAttributeDTO> savedAttributes = new ArrayList<>();

        for (AttributeDTO.SaveAttributeDTO attributeDTO : attributeDTOs) {
            AttributeDTO.GetAttributeDTO savedAttribute = saveOrUpdate(attributeDTO);
            savedAttributes.add(savedAttribute);
        }

        return savedAttributes;
    }

    @Override
    @Transactional
    public String deleteAllByMedicineId(Long medicineId) {
        if (!medicineRepository.existsById(medicineId)) {
            throw new RuntimeException("Không tìm thấy thuốc với ID: " + medicineId);
        }

        // Đếm số thuộc tính trước khi xóa
        long count = attributeRepository.countByMedicineId(medicineId);

        if (count == 0) {
            return "Không có thuộc tính nào để xóa";
        }

        // Thực hiện xóa mềm các thuộc tính
        attributeRepository.softDeleteByMedicineId(medicineId);

        return "Đã xóa thành công " + count + " thuộc tính của thuốc";
    }
}