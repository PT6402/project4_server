package fpt.aptech.project4_server.dto.book;

import fpt.aptech.project4_server.dto.author.AuthorShow;
import fpt.aptech.project4_server.dto.category.CateShow;
import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.entities.book.Category;
import jakarta.persistence.Lob;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BooklistUserRes {

    private int id;
    private String name;
    private double price;
    private double rating;
    private int ratingQuantity;
     List<AuthorShow> authorlist;
    List<CateShow> catelist;
    @Lob
    private byte[] fileimage;
}
