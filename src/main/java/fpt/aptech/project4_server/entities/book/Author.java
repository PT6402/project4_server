package fpt.aptech.project4_server.entities.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tbAuthor")
public class Author extends BaseEntity {

    private String name;
    @Lob
    private byte[] Image_data;

    @ManyToMany(mappedBy = "authors")
    @JsonIgnore
    private List<Book> books;
}
