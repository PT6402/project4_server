package fpt.aptech.project4_server.dto.book;

import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.entities.book.Category;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookAdCreateRes {

    private String name;
    private double price;
    private int pageQuantity;
    private String edition;
    private String publisherDescription;
    private double rating;
    private int ratingQuantity;
    List<Author> authorlist;
    private MultipartFile file;
    List<Category> catelist;
}
