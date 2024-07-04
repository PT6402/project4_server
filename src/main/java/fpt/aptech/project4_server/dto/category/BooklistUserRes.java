/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.category;

import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.entities.book.Category;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author macos
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BooklistUserRes {
    private int id;
     private String name;
    private double price;
    private double rating;
    private int ratingQuantity;
    private byte[] fileimage;
    List<Category> catelist;
}
