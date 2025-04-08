package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.CategoryDTO;
import hunre.edu.vn.backend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parent.id")
    CategoryDTO.GetCategoryDTO toGetCategoryDTO(Category entity);

    Category toEntity(CategoryDTO.SaveCategoryDTO dto);
}