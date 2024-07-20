/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.order;

import jakarta.persistence.Lob;
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
public class OrderDetailDto {
    private int id;
//    private int orderId;
    private String bookName;
    private int bookId;
    private int dayQuantity;
    private int packId;
    private Double price;
    private String packName;
       @Lob
    private byte[] ImageCove;
       
}
