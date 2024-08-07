/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.book;

import fpt.aptech.project4_server.dto.author.AuthorShow;

import jakarta.persistence.Lob;
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
public class BookPagnination {

    private int bookid;
    private String name;
    private double rating;
    private int ratingQuantity;
    private List<AuthorShow> authors;
    private double price;
    @Lob
    private byte[] ImageCove;

}
