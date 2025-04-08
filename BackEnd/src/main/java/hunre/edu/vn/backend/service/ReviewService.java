package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.dto.ReviewDTO;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewDTO.GetReviewDTO> findAll();
    Optional<ReviewDTO.GetReviewDTO> findById(Long id);
    ReviewDTO.GetReviewDTO saveOrUpdate(ReviewDTO.SaveReviewDTO reviewDTO);
    String deleteByList(List<Long> ids);
    List<ReviewDTO.GetReviewDTO> findByUserId(Long userId);
    List<ReviewDTO.GetReviewDTO> findByRating(Integer rating);
    List<ReviewDTO.GetReviewDTO> findByDoctorId(Long doctorId);
    List<ReviewDTO.GetReviewDTO> findByMedicineId(Long medicineId);
    List<ReviewDTO.GetReviewDTO> findByMedicineIdAndUserId(Long medicineId, Long userId);
}