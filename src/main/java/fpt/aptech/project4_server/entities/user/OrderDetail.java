package fpt.aptech.project4_server.entities.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import fpt.aptech.project4_server.entities.BaseEntity;
import fpt.aptech.project4_server.entities.book.Book;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tbOrderDetail")
public class OrderDetail extends BaseEntity {

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private Integer dayQuantity;
    private Integer packId;
    private Double Price;

}
