package fpt.aptech.project4_server.dto.order;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
