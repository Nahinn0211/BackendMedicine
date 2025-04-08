package hunre.edu.vn.backend.mapper;


import hunre.edu.vn.backend.dto.SalaryDTO;
import hunre.edu.vn.backend.entity.Salary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalaryMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    SalaryDTO.GetSalaryDTO toGetSalaryDTO(Salary entity);

    Salary toSalaryEntity(SalaryDTO.SaveSalaryDTO dto);
}