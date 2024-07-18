/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fpt.aptech.project4_server.dto.cart;

import fpt.aptech.project4_server.dto.packageread.PackageShowbook;
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
public class CartItemShow {
    private int cartItemId;
    private int bookId;
    private String bookName;
    private Double priceBuy;
    // private Double priceRent;
    private int packId;
    private Boolean ibuy;
    private List<PackageShowbook> packlist;
    private byte[] imageData;
}
