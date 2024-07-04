package fpt.aptech.project4_server.entities.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbFilePdf")
public class FilePdf extends BaseEntity {

    private String file_name;
 
    private String file_type;
    
    @Lob
    private byte[] file_data;
   
    
    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;
    
    @OneToMany(mappedBy = "pdf")
    @JsonIgnore
    private List<ImagesBook> imagesbook;
}
