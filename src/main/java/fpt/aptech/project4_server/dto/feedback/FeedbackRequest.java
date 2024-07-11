package fpt.aptech.project4_server.dto.feedback;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    private String content;
    private LocalDateTime feedbackDate;
    private int userDetailId;
}
