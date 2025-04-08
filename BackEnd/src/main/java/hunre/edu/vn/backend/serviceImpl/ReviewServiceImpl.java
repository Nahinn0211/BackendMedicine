package hunre.edu.vn.backend.serviceImpl;

import hunre.edu.vn.backend.dto.ReviewDTO;
import hunre.edu.vn.backend.entity.DoctorProfile;
import hunre.edu.vn.backend.entity.Medicine;
import hunre.edu.vn.backend.entity.Review;
import hunre.edu.vn.backend.entity.User;
import hunre.edu.vn.backend.mapper.ReviewMapper;
import hunre.edu.vn.backend.repository.*;
import hunre.edu.vn.backend.service.ReviewService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorRepository;
    private final MedicineRepository medicineRepository;
    private final ServiceRepository serviceRepository;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            DoctorProfileRepository doctorRepository,
            MedicineRepository medicineRepository,
            ServiceRepository serviceRepository,
            ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.medicineRepository = medicineRepository;
        this.serviceRepository = serviceRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public List<ReviewDTO.GetReviewDTO> findAll() {
        return reviewRepository.findAllActive()
                .stream()
                .map(reviewMapper::toGetReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReviewDTO.GetReviewDTO> findById(Long id) {
        return reviewRepository.findActiveById(id)
                .map(reviewMapper::toGetReviewDTO);
    }

    @Override
    @Transactional
    public ReviewDTO.GetReviewDTO saveOrUpdate(ReviewDTO.SaveReviewDTO reviewDTO) {
        Review review;

        if (reviewDTO.getId() == null || reviewDTO.getId() == 0) {
            // INSERT case
            review = new Review();
            review.setCreatedAt(LocalDateTime.now());
            review.setUpdatedAt(LocalDateTime.now());
        } else {
            // UPDATE case
            Optional<Review> existingReview = reviewRepository.findActiveById(reviewDTO.getId());
            if (existingReview.isEmpty()) {
                throw new RuntimeException("Review not found with ID: " + reviewDTO.getId());
            }
            review = existingReview.get();
            review.setUpdatedAt(LocalDateTime.now());
        }

        // Xử lý user relationship
        if (reviewDTO.getUserId() != null) {
            User user = userRepository.findById(reviewDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + reviewDTO.getUserId()));
            review.setUser(user);
        }

        // Xử lý doctor relationship (nếu có)
        if (reviewDTO.getDoctorId() != null) {
            DoctorProfile doctor = doctorRepository.findActiveById(reviewDTO.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + reviewDTO.getDoctorId()));
            review.setDoctor(doctor);
        }

        // Xử lý medicine relationship (nếu có)
        if (reviewDTO.getMedicineId() != null) {
            Medicine medicine = medicineRepository.findActiveById(reviewDTO.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + reviewDTO.getMedicineId()));
            review.setMedicine(medicine);
        }

        // Cập nhật các trường khác
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toGetReviewDTO(savedReview);
    }

    @Override
    public String deleteByList(List<Long> ids) {
        for (Long id : ids) {
            if (reviewRepository.existsById(id)) {
                reviewRepository.softDelete(id);
            }
        }

        return "Đã xóa thành công " + ids.size() + " đánh giá";
    }

    @Override
    public List<ReviewDTO.GetReviewDTO> findByUserId(Long userId) {
        return reviewRepository.findByUser_Id(userId)
                .stream()
                .map(reviewMapper::toGetReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO.GetReviewDTO> findByRating(Integer rating) {
        return reviewRepository.findByRating(rating)
                .stream()
                .map(reviewMapper::toGetReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO.GetReviewDTO> findByDoctorId(Long doctorId) {
        return reviewRepository.findByDoctor_Id(doctorId)
                .stream()
                .map(reviewMapper::toGetReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO.GetReviewDTO> findByMedicineId(Long medicineId) {
        return reviewRepository.findByMedicine_Id(medicineId)
                .stream()
                .map(reviewMapper::toGetReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewDTO.GetReviewDTO> findByMedicineIdAndUserId(Long medicineId, Long userId) {
        return reviewRepository.findByMedicine_IdAndUser_Id(medicineId, userId)
                .stream()
                .map(reviewMapper::toGetReviewDTO)
                .collect(Collectors.toList());
    }
}