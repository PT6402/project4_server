package fpt.aptech.project4_server.service;

import com.stripe.exception.StripeException;

import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.response.PaymentResponse;
import org.springframework.stereotype.Service;
@Service
public interface PaymentService {

   public PaymentResponse createPaymentLink(Order order) throws StripeException;
}
