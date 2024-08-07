package fpt.aptech.project4_server.entities.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "tbFilePdf")
public class FilePdf extends BaseEntity {

    private String file_name;
 
    private String file_type;
    
    @Lob
    private byte[] file_data;
   
    
    @OneToOne
    @JoinColumn(name = "book_id")
    @JsonIgnore
    private Book book;
    
    @OneToMany(mappedBy = "pdf",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<ImagesBook> imagesbook;
    
    @Override
    public int hashCode() {
        return Objects.hash(id, file_name); // Thay đổi theo các thuộc tính cần thiết
    }
}
