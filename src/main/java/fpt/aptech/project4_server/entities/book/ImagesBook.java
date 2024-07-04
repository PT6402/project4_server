/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.entities.book;

import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author macos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Entity
@Table(name = "tbImagesBook")
public class ImagesBook extends BaseEntity {

    private String Image_name;
     @Lob
    @Column(name = "image_data", columnDefinition = "VARBINARY(MAX)")
    private byte[] Image_data;
    private boolean cover;

    @ManyToOne
    @JoinColumn(name = "filepdf_id")
    private FilePdf pdf;
}
