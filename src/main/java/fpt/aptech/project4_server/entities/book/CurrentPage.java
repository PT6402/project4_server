/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.entities.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fpt.aptech.project4_server.entities.BaseEntity;
import fpt.aptech.project4_server.entities.user.Mybook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author macos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "tbCurrentPage")
public class CurrentPage extends BaseEntity {
    private int CurrenPageIndex;
     @Lob
    @Column(name = "image_data", columnDefinition = "VARBINARY(MAX)")
    private byte[] ImagePageData;
     
     @OneToOne(mappedBy="currentpage")
   private Mybook mybook;
     
     @OneToMany(mappedBy = "currentpage")
    @JsonIgnore
    private List<ImageRead> imagesread;
}
