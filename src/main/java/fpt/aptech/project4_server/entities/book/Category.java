package fpt.aptech.project4_server.entities.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Category extends BaseEntity{
    private String name;
    private String description;
    private String pathImage;

    
    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Book> books;
}
