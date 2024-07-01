package fpt.aptech.project4_server.entities.book;

import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
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
@Table(name = "tbCategory")
public class Category extends BaseEntity {
    private String name;

    @ManyToMany(mappedBy = "categories")
    private List<Book> books;
}
