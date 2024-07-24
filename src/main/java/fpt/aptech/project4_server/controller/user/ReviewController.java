package fpt.aptech.project4_server.controller.user;

import fpt.aptech.project4_server.dto.review.ReviewCreateDTO;
import fpt.aptech.project4_server.dto.review.ReviewUpdateDTO;
import fpt.aptech.project4_server.entities.book.Review;
import fpt.aptech.project4_server.service.ForbiddenWordsService;
import fpt.aptech.project4_server.service.ReviewService;
import fpt.aptech.project4_server.util.ResultDto;
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
public class ReviewController {

    @Autowired
    ReviewService reviewService;
    @Autowired
    ForbiddenWordsService forbidden;

    @GetMapping("")
    public ResponseEntity<ResultDto<List<Review>>> getReviews() {
        return reviewService.getReviews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto<Review>> getReview(@PathVariable("id") Integer id) {
        return reviewService.getReview(id);
    }

    @PostMapping("")
    public ResponseEntity<ResultDto<?>> createReview(@RequestBody ReviewCreateDTO reviewRequest) {
        if (forbidden.containsForbiddenWord(reviewRequest.getContent())) {
            ResultDto<String> result = new ResultDto<>();
            result.setStatus(false);
            result.setMessage("Your review contain forbidden words");
            return ResponseEntity.badRequest().body(result);
        }
        return reviewService.createReview(reviewRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDto<?>> updateReview(@PathVariable("id") int id,
            @RequestBody ReviewUpdateDTO reviewUpdateDTO) {
        if (forbidden.containsForbiddenWord(reviewUpdateDTO.getContent())) {
            ResultDto<String> result = new ResultDto<>();
            result.setStatus(false);
            result.setMessage("Your review contain forbidden words");
            return ResponseEntity.badRequest().body(result);
        }
        return reviewService.updateReview(id, reviewUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDto<Void>> deleteReview(@PathVariable("id") int id) {
        return reviewService.deleteReview(id);
    }
}
