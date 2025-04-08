package hunre.edu.vn.backend.mapper;

import hunre.edu.vn.backend.dto.ReviewDTO;
import hunre.edu.vn.backend.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "medicineId", source = "medicine.id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ReviewDTO.GetReviewDTO toGetReviewDTO(Review entity);

    Review toReviewEntity(ReviewDTO.SaveReviewDTO dto);
}