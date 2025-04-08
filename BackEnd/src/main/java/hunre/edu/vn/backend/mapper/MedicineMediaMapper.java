package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.MedicineMediaDTO;
import hunre.edu.vn.backend.entity.MedicineMedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MedicineMediaMapper {
    @Mapping(target = "medicineId", source = "medicine.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    MedicineMediaDTO.GetMedicineMediaDTO toGetMedicineMediaDTO(MedicineMedia entity);

    MedicineMedia toMedicineMediaEntity(MedicineMediaDTO.SaveMedicineMediaDTO dto);
}