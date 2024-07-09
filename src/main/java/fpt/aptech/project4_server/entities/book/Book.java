package fpt.aptech.project4_server.entities.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.aptech.project4_server.entities.BaseEntity;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.entities.user.Wishlist;
import fpt.aptech.project4_server.entities.user.Cart;
import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.Data;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tbBook")
public class Book extends BaseEntity {

    private String name;
    private double price;
    private int pageQuantity;
    private String edition;
    private String publisherDescription;
    private double rating;
    private int ratingQuantity;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)

    @JsonIgnore
    private List<Page> pages;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Mybook> mybook;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Wishlist> wishlist;

    @ManyToMany
    @JoinTable(
            name = "Author_Book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )

    private List<Author> authors;

    @ManyToMany
    @JoinTable(
            name = "Category_Book",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "cate_id")
    )
    private List<Category> categories;

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private FilePdf filePdf;


    
    @ManyToMany(mappedBy = "books")
    @JsonIgnore
    private List<Cart> carts;
    
}
