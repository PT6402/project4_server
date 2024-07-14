package fpt.aptech.project4_server.service;

import com.stripe.exception.StripeException;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.entities.user.Cart;
import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPaymentLink(Order order) throws StripeException;
}
