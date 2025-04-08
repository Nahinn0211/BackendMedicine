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
        return attributeRepository.findByMedicineId(medicineId)
                .stream()
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
        return "Deleted Successfully";
    }
}