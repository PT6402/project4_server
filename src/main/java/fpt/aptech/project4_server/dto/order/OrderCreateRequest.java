package fpt.aptech.project4_server.dto.order;

import java.util.List;

import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {
    private int userId;
    private List<CartItemAddRequest> cartItems;
}
