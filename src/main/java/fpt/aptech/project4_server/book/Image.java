package fpt.aptech.project4_server.book;

import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbImage")
public class Image extends BaseEntity {

    private String cover;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
