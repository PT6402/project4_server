package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.review.ReviewCreateDTO;
import fpt.aptech.project4_server.dto.review.ReviewUpdateDTO;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Review;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.BookRepo;

import fpt.aptech.project4_server.repository.ReviewRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final BookRepo bookRepository;

    private final UserDetailRepo userDetailRepository;

    private final ForbiddenWordsService forbidden;

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

    // -------
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

    // -------
    public ResponseEntity<ResultDto<?>> createReview(int userId, ReviewCreateDTO reviewRequest) {
        try {
            // forbidden word
            if (forbidden.containsForbiddenWord(reviewRequest.getContent())) {
                throw new Exception("Your review contain forbidden words");
            }
            // Tìm book
            var bookOptional = bookRepository.findById(reviewRequest.getBookId())
                    .orElseThrow(() -> new Exception("book not found"));

            // Tìm user
            UserDetail userDetail = userDetailRepository.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));

            // Kiểm tra xem review đã tồn tại chưa
            var existingReview = reviewRepository.findByBookIdAndUserDetailId(userDetail.getId(),
                    reviewRequest.getBookId());
            if (existingReview.isPresent()) {
                ResultDto<?> response = ResultDto.builder().status(false)
                        .message("User has already reviewed this book!").build();
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Tạo review mới
            var newReview = Review.builder()
                    .content(reviewRequest.getContent())
                    .rating(reviewRequest.getRating())
                    .book(bookOptional)
                    .userDetail(userDetail)
                    .build();

            var reviewSaved = reviewRepository.save(newReview);
            bookOptional.setRatingQuantity(bookOptional.getRatingQuantity() + 1);
            List<Review> reviewList = reviewRepository.findByBookId(reviewRequest.getBookId());

            double totalRating = reviewList.stream()
                    .mapToDouble(Review::getRating)
                    .sum();
            double averageRating = totalRating / bookOptional.getRatingQuantity();
            bookOptional.setRating(averageRating);
            bookRepository.save(bookOptional);

            HashMap<String, Object> result = new HashMap<>();
            result.put("id", reviewSaved.getId());
            result.put("content", reviewSaved.getContent());
            result.put("star", reviewSaved.getRating());

            ResultDto<?> response = ResultDto.builder().status(true).message("Review created successfully!")
                    .model(result).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResultDto<?>> updateReview(int userId, ReviewUpdateDTO reviewUpdateDTO) {
        try {
            // forbidden word
            if (forbidden.containsForbiddenWord(reviewUpdateDTO.getContent())) {
                throw new Exception("Your review contain forbidden words");
            }

            // find and update
            var review = reviewRepository.findById(reviewUpdateDTO.getId())
                    .orElseThrow(() -> new Exception("review not found"));
            review.setContent(reviewUpdateDTO.getContent());
            review.setRating(reviewUpdateDTO.getRating());
            Review updateReview = reviewRepository.save(review);

            // update rating for book
            List<Review> reviewlist = reviewRepository.findByBookId(updateReview.getBook().getId());
            var bookOptional = bookRepository.findById(updateReview.getBook().getId());
            Book book = bookOptional.get();
            double totalRat = reviewlist.stream()
                    .mapToDouble(Review::getRating)
                    .sum();
            double avarageRat = totalRat / book.getRatingQuantity();
            book.setRating(avarageRat);
            bookRepository.save(book);

            HashMap<String, Object> result = new HashMap<>();
            result.put("id", updateReview.getId());
            result.put("content", updateReview.getContent());
            result.put("star", updateReview.getRating());

            ResultDto<?> response = ResultDto.builder()
                    .status(true)
                    .message("Review updated successfully!")
                    .model(result)
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
