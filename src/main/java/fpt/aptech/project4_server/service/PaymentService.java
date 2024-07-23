package fpt.aptech.project4_server.service;

import com.stripe.exception.StripeException;

import fpt.aptech.project4_server.dto.payment.PaymentResponse;
import fpt.aptech.project4_server.entities.user.Order;

import org.springframework.stereotype.Service;

@Service
public interface PaymentService {

   public PaymentResponse createPaymentLink(Order order) throws StripeException;
}
