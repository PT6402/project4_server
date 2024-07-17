package fpt.aptech.project4_server.dto.book;

import fpt.aptech.project4_server.dto.author.AuthorUserRes;
import fpt.aptech.project4_server.dto.category.CateShow;
import fpt.aptech.project4_server.dto.packageread.PackageShowbook;
import fpt.aptech.project4_server.dto.review.ReviewShow1;
import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.entities.book.Review;
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
public class BookUserRes {

    private int id;
    private String name;
    List<PackageShowbook> packlist;
    private double priceBuy;
    private int pageQuantity;
    private String edition;
    private String publisherDescription;
    private double rating;
    private int ratingQuantity;
    List<AuthorUserRes> authorlist;
    List<ReviewShow1> reviewlist;
    @Lob      
    private byte[] fileimage;
    List<CateShow> catelist;
}
