package fpt.aptech.project4_server.controller.user;

import fpt.aptech.project4_server.dto.review.ReviewCreateDTO;
import fpt.aptech.project4_server.dto.review.ReviewUpdateDTO;
import fpt.aptech.project4_server.entities.book.Review;
import fpt.aptech.project4_server.security.CurrentUser;
import fpt.aptech.project4_server.security.UserGlobal;
import fpt.aptech.project4_server.service.ForbiddenWordsService;
import fpt.aptech.project4_server.service.ReviewService;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("")
    public ResponseEntity<ResultDto<List<Review>>> getReviews() {
        return reviewService.getReviews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto<Review>> getReview(@PathVariable("id") Integer id) {
        return reviewService.getReview(id);
    }

    // admin
    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDto<Void>> deleteReview(@PathVariable("id") int id) {
        return reviewService.deleteReview(id);
    }

    // ----------
    @PostMapping
    public ResponseEntity<ResultDto<?>> createReview(@CurrentUser UserGlobal user,
            @RequestBody ReviewCreateDTO reviewRequest) {
        return reviewService.createReview(user.getId(), reviewRequest);
    }

    @PutMapping
    public ResponseEntity<ResultDto<?>> updateReview(@CurrentUser UserGlobal user,
            @RequestBody ReviewUpdateDTO reviewUpdateDTO) {
        return reviewService.updateReview(user.getId(), reviewUpdateDTO);
    }
}
