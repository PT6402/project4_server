package fpt.aptech.project4_server.entities.book;

import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    private String img_name;
    private String file_type;
    
    @Lob
    private byte[] file_data;
      @Lob
    private byte[] img_data;
    
    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
