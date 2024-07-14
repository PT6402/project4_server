package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.feedback.FeedbackDTO;
import fpt.aptech.project4_server.dto.feedback.FeedbackRequest;
import fpt.aptech.project4_server.entities.book.Feedback;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.FeedbackRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    UserDetailRepo userDetailRepository;
    
     public ResponseEntity<ResultDto<?>> saveFeedback(FeedbackRequest feedbackRequest) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepository.findById(feedbackRequest.getUserDetailId());
            if (userDetailOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder().status(false).message("UserDetail not found").build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();

            Feedback feedback = Feedback.builder()
                    .content(feedbackRequest.getContent())
                    .feedbackDate(feedbackRequest.getFeedbackDate())
                    .userDetail(userDetail)
                    .build();

            feedbackRepository.save(feedback);

            return new ResponseEntity<>(ResultDto.builder().status(true).message("Feedback saved successfully").build(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> getAllFeedback() {
        try {
            List<Feedback> feedbackList = feedbackRepository.findAll();
            List<FeedbackDTO> feedbackDTOList = feedbackList.stream().map(this::convertToDTO).collect(Collectors.toList());
            return new ResponseEntity<>(ResultDto.builder().status(true).message("Feedback retrieved successfully").model(feedbackDTOList).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> getFeedbackById(int id) {
        try {
            Optional<Feedback> feedbackOptional = feedbackRepository.findById(id);
            if (feedbackOptional.isPresent()) {
                return new ResponseEntity<>(ResultDto.builder().status(true).message("Feedback retrieved successfully").model(feedbackOptional.get()).build(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(ResultDto.builder().status(false).message("Feedback not found").build(), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> deleteFeedbackById(int id) {
        try {
            feedbackRepository.deleteById(id);
            return new ResponseEntity<>(ResultDto.builder().status(true).message("Feedback deleted successfully").build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     private FeedbackDTO convertToDTO(Feedback feedback) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setFeedbackId(feedback.getId());
        feedbackDTO.setContent(feedback.getContent());
        feedbackDTO.setFeedbackDate(feedback.getFeedbackDate());

        if (feedback.getUserDetail() != null && feedback.getUserDetail().getUser() != null) {
            feedbackDTO.setUserEmail(feedback.getUserDetail().getUser().getEmail());
        } else {
            feedbackDTO.setUserEmail("N/A"); // or any default value you prefer
        }
        return feedbackDTO;
    }
}
