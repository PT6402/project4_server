package fpt.aptech.project4_server.dto.order;

import java.util.HashMap;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {

    private int id;
    private String bookName;
    private int bookId;
    private Double price;
    private Integer dayPackage;

    @Lob
    private byte[] image;

    private int dayQuantity;
    private int packId;
    private String packName;

    private HashMap<String, Object> review;

}
