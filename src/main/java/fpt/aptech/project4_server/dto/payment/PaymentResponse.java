package fpt.aptech.project4_server.dto.payment;

import lombok.Data;

@Data
public class PaymentResponse {

    private String payment_url;
    private String sessionId;

}
