/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.category;

import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.Review;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author macos
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookUserRes {
     private int id;
     private String name;
    private double price;
      private int pageQuantity;
    private String edition;
        private String publisherDescription;
    private double rating;
    private int ratingQuantity;
      List<Author> authorlist;
      List<Review> reviewlist;
   List<byte[]> fileimagelist;
    List<Category> catelist; 
}
