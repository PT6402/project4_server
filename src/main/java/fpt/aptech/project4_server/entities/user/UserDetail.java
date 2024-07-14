package fpt.aptech.project4_server.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.aptech.project4_server.entities.book.Feedback;
import fpt.aptech.project4_server.entities.book.Review;
import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbUserDetail")
@SuperBuilder
//@EqualsAndHashCode(callSuper = true)
public class UserDetail extends BaseEntity {

    @Column(name = "fullname", columnDefinition = "nvarchar(200)")
    private String fullname;
    @Lob
    private byte[] avartar;
    private Integer current_book_id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "userDetail")
    private Cart cart;


    @OneToMany(mappedBy = "userDetail")
    @JsonIgnore
    private List<Review> reviews;


    @OneToMany(mappedBy = "userDetail")
    private List<Feedback> feedbacks;

    @ManyToMany(mappedBy = "userDetails")
    @JsonIgnore
    private List<Order> orders;


    @OneToMany(mappedBy = "userDetail")
    @JsonIgnore
    private List<Mybook> mybook;
    
    @OneToMany(mappedBy = "userDetail")
    @JsonIgnore
    private List<Wishlist> wishlist;

}
