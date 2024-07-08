package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.review.ReviewCreateDTO;
import fpt.aptech.project4_server.dto.review.ReviewUpdateDTO;
import fpt.aptech.project4_server.entities.book.Review;
import fpt.aptech.project4_server.repository.BookRepo;

import fpt.aptech.project4_server.repository.ReviewRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    BookRepo bookRepository;

    @Autowired
    UserDetailRepo userDetailRepository;

    public ResponseEntity<ResultDto<List<Review>>> getReviews() {
        try {
            List<Review> reviews = reviewRepository.findAll();
            ResultDto<List<Review>> response = ResultDto.<List<Review>>builder()
                    .status(true)
                    .message("Success")
                    .model(reviews)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<List<Review>> response = ResultDto.<List<Review>>builder()
                    .status(false)
                    .message("Failed to retrieve authors")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<Review>> getReview(int id) {
        try {
            Optional<Review> reviewOptional = reviewRepository.findById(id);
            if (reviewOptional.isPresent()) {
                ResultDto<Review> response = ResultDto.<Review>builder()
                        .status(true)
                        .message("Success")
                        .model(reviewOptional.get())
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Review> response = ResultDto.<Review>builder()
                        .status(false)
                        .message("Review not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Review> response = ResultDto.<Review>builder()
                    .status(false)
                    .message("Failed to retrieve review")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> createReview(ReviewCreateDTO reviewRequest) {
        try {
            // Tim book
            var bookOptional = bookRepository.findById(reviewRequest.getBookId());
            if (bookOptional.isEmpty()) {
                ResultDto<?> response = ResultDto.builder().status(false).message("Book not found!").build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Tim user
            var userOptional = userDetailRepository.findById(reviewRequest.getUserId());
            if (userOptional.isEmpty()) {
                ResultDto<?> response = ResultDto.builder().status(false).message("User not found!").build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Tao review moi
            var newReview = Review.builder()
                    .content(reviewRequest.getContent())
                    .rating(reviewRequest.getRating())
                    .book(bookOptional.get())
                    .userDetail(userOptional.get())
                    .build();

            reviewRepository.save(newReview);
            ResultDto<?> response = ResultDto.builder().status(true).message("Review created successfully!").build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message("Failed to create review!").build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> updateReview(int id, ReviewUpdateDTO reviewUpdateDTO) {
        try {
            Optional<Review> reviewOptional = reviewRepository.findById(id);
            if (reviewOptional.isPresent()) {
                Review review = reviewOptional.get();
                review.setContent(reviewUpdateDTO.getContent());
                review.setRating(reviewUpdateDTO.getRating());

                Review updateReview = reviewRepository.save(review);

                ResultDto<Review> response = ResultDto.<Review>builder()
                        .status(true)
                        .message("Review updated successfully!")
                        .model(updateReview)
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Review> response = ResultDto.<Review>builder()
                        .status(false)
                        .message("Review not found!")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Review> response = ResultDto.<Review>builder()
                    .status(false)
                    .message("Failed to update review!")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<Void>> deleteReview(int id) {
        try {
            Optional<Review> reviewOptional = reviewRepository.findById(id);
            if (reviewOptional.isPresent()) {
                reviewRepository.deleteById(id);

                ResultDto<Void> response = ResultDto.<Void>builder()
                        .status(true)
                        .message("Review deleted successfully")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Void> response = ResultDto.<Void>builder()
                        .status(false)
                        .message("Review not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Void> response = ResultDto.<Void>builder()
                    .status(false)
                    .message("Failed to delete review")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
