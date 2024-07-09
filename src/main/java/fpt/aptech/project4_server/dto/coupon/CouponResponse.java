package fpt.aptech.project4_server.dto.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {

    private int id;
    private String code;
    private double discountRate;
}
