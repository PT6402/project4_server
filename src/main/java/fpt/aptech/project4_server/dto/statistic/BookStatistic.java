package fpt.aptech.project4_server.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookStatistic {
    private Integer bookId;
    private String bookName;
    private Long totalBooks;
    private Long boughtBooks;
    private Long rentedBooks;
    private Double sales;
}
