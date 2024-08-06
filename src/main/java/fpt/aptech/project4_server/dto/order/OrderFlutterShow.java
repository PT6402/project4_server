package fpt.aptech.project4_server.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.HashMap;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderFlutterShow {
    private int bookId;
    private String bookName;
    private Double price;
    private Integer dayPackage;
    private byte[] image;
    private List<HashMap<String, Object>> reviews;
}
