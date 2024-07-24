package fpt.aptech.project4_server.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import fpt.aptech.project4_server.dto.payment.PaymentResponse;
import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.service.jwt.JwtService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PaymentServiceImpl implements PaymentService {

        @Value("${stripe.api.key}")
        private String stripeSecretKey;
        @Autowired
        JwtService jservice;

        @Override
        public PaymentResponse createPaymentLink(Order order) throws StripeException {
                Stripe.apiKey = stripeSecretKey;

                // Lấy tổng giá trị từ đơn hàng
                double totalPrice = order.getTotalPrice();
                long unitAmount = Math.round(totalPrice * 100); // Chuyển đổi thành cents

                // Tạo một dòng hàng với tổng giá trị
                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                                .setQuantity(1L) // Số lượng cố định 1
                                .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                                .setCurrency("usd")
                                                                .setUnitAmount(unitAmount)
                                                                .setProductData(
                                                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                                                                .builder()
                                                                                                .setName("Order #"
                                                                                                                + order.getId()) // Tên
                                                                                                                                 // sản
                                                                                                                                 // phẩm
                                                                                                                                 // có
                                                                                                                                 // thể
                                                                                                                                 // là
                                                                                                                                 // ID
                                                                                                                                 // đơn
                                                                                                                                 // hàng
                                                                                                                                 // hoặc
                                                                                                                                 // tên
                                                                                                                                 // tùy
                                                                                                                                 // chỉnh
                                                                                                .build())
                                                                .build())
                                .build();

                List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
                lineItems.add(lineItem);

                String token = jservice.generatePaymentToken(order.getUserDetail().getUser().getEmail());
                // Tạo phiên thanh toán
                SessionCreateParams params = SessionCreateParams.builder()
                                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl("http://localhost:3000/payment/success/" + order.getId() + "/" + token)

                                .setCancelUrl("http://localhost:3000/payment/fail")

                                .addAllLineItem(lineItems)
                                .build();

                // Tạo session Stripe
                Session session = Session.create(params);

                // Trả về URL thanh toán
                PaymentResponse res = new PaymentResponse();
                res.setPayment_url(session.getUrl());

                res.setSessionId(session.getId());
                return res;
        }
}
