package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.feedback.FeedbackRequest;
import fpt.aptech.project4_server.service.FeedbackService;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ResultDto<?>> createFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        return feedbackService.saveFeedback(feedbackRequest);
    }

    @GetMapping
    public ResponseEntity<ResultDto<?>> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto<?>> getFeedbackById(@PathVariable int id) {
        return feedbackService.getFeedbackById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultDto<?>> deleteFeedbackById(@PathVariable int id) {
        return feedbackService.deleteFeedbackById(id);
    }
}
