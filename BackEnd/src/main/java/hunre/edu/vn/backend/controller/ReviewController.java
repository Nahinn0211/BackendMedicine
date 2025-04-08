package hunre.edu.vn.backend.controller;

import hunre.edu.vn.backend.dto.ReviewDTO;
import hunre.edu.vn.backend.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO.GetReviewDTO>> getAllReviews() {
        List<ReviewDTO.GetReviewDTO> reviews = reviewService.findAll();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO.GetReviewDTO> getReviewById(@PathVariable Long id) {
        return reviewService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<ReviewDTO.GetReviewDTO> saveOrUpdateReview(@RequestBody ReviewDTO.SaveReviewDTO reviewDTO) {
        ReviewDTO.GetReviewDTO savedReview = reviewService.saveOrUpdate(reviewDTO);
        return ResponseEntity.ok(savedReview);
    }

    @DeleteMapping("/{id}")
    public String deleteReview(@RequestBody List<Long> ids) {
        return reviewService.deleteByList(ids);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<ReviewDTO.GetReviewDTO>> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewDTO.GetReviewDTO> reviews = reviewService.findByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/by-rating/{rating}")
    public ResponseEntity<List<ReviewDTO.GetReviewDTO>> getReviewsByRating(@PathVariable Integer rating) {
        List<ReviewDTO.GetReviewDTO> reviews = reviewService.findByRating(rating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/by-doctor/{doctorId}")
    public ResponseEntity<List<ReviewDTO.GetReviewDTO>> getReviewsByDoctorId(@PathVariable Long doctorId) {
        List<ReviewDTO.GetReviewDTO> reviews = reviewService.findByDoctorId(doctorId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/by-medicine/{medicineId}")
    public ResponseEntity<List<ReviewDTO.GetReviewDTO>> getReviewsByMedicineId(@PathVariable Long medicineId) {
        List<ReviewDTO.GetReviewDTO> reviews = reviewService.findByMedicineId(medicineId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/by-medicine/{medicineId}/and-user/{userId}")
    public ResponseEntity<List<ReviewDTO.GetReviewDTO>> getReviewsByMedicineIdAndUserId(@PathVariable Long medicineId, @PathVariable Long userId) {
        List<ReviewDTO.GetReviewDTO> reviews = reviewService.findByMedicineIdAndUserId(medicineId, userId);
        return ResponseEntity.ok(reviews);
    }
}