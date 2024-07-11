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
public class FeedbackDTO {

    private int feedbackId;
    private String content;
    private LocalDateTime feedbackDate;
    private String userEmail;
}
