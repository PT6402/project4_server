package fpt.aptech.project4_server.dto.order;

import fpt.aptech.project4_server.entities.book.Book;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {

    private int userId;
    private List<Book> bookIds;
}
