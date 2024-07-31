package fpt.aptech.project4_server.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewUpdateDTO {
    private int id;
    private String content;
    private double rating;
}
