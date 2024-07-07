package fpt.aptech.project4_server.dto.order;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequest {
    private int paymentStatus;
    private List<Integer> bookIds;
}
