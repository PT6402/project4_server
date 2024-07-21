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
public class PaymentCheck {
    private int orderId;
    private int userDetailId;
    @Lob
    private String token;
}
