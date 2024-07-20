package fpt.aptech.project4_server.service;

import com.stripe.exception.StripeException;
import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import fpt.aptech.project4_server.dto.order.OrderCreateRequest;
import fpt.aptech.project4_server.dto.order.OrderUpdateRequest;
import fpt.aptech.project4_server.dto.order.PaymentCheck;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.entities.user.Cart;
import fpt.aptech.project4_server.entities.user.CartItem;
import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.entities.user.OrderDetail;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.*;
import fpt.aptech.project4_server.response.PaymentResponse;
import fpt.aptech.project4_server.util.ResultDto;
import java.time.LocalDateTime;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private JwtService jservice;
    @Autowired
    private UserDetailRepo userDetailRepo;
    @Autowired
    private MyBookService MBservice;
    @Autowired
    private CartService cartservice;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PackageReadRepository packageReadRepository;

    public ResponseEntity<ResultDto<?>> checkoutCart(int userId, int cartId) {
        try {
            // Kiểm tra xem user có tồn tại hay không
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userId);
            System.out.println(userDetailOptional.get().getId());
            if (userDetailOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder().status(false).message("User not found").build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();

            // Lấy Cart dựa vào cartId
            Optional<Cart> cartOptional = cartRepository.findById(cartId);
            if (cartOptional.isEmpty() || cartOptional.get().getCartItems().isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder().status(false).message("Cart is empty or not found").build(), HttpStatus.BAD_REQUEST);
            }

            Cart cart = cartOptional.get();
            List<CartItem> cartItems = cart.getCartItems();
            List<OrderDetail> orderDetails = new ArrayList<>();
            double totalPrice = 0.0;

            // Tạo Order mới
            Order order = Order.builder()
                    .userDetail(userDetail)
                    .paymentStatus(0)
                    .orderDetails(orderDetails)
                    .dateOrder(LocalDateTime.now())
                    .build();

            for (CartItem cartItem : cartItems) {
                Book book = cartItem.getBook();
                double price = cartItem.getPrice(); // Lấy giá trực tiếp từ CartItem
                totalPrice += price;

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setBook(book);
                orderDetail.setDayQuantity(cartItem.getDayQuantity());
                orderDetail.setPrice(price); // Lấy giá từ CartItem
                orderDetail.setPackId(cartItem.getPackId());
                orderDetails.add(orderDetail);
            }

            order.setOrderDetails(orderDetails);
            order.setTotalPrice(totalPrice);
            Order savedOrder = orderRepository.save(order);

            // Lưu OrderDetail sau khi Order đã được lưu
            for (OrderDetail orderDetail : orderDetails) {
                orderDetail.setOrder(savedOrder);
                orderDetailRepository.save(orderDetail);
            }

            // Tạo link thanh toán
            PaymentResponse paymentResponse = paymentService.createPaymentLink(savedOrder);
            cartservice.clearCart(cartId);
            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Order created successfully")
                    .model(paymentResponse)
                    .build(), HttpStatus.CREATED);
        } catch (StripeException e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message("Payment error: " + e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> checkPayment(PaymentCheck paycheck) {
        try {
            String namecheck = jservice.extractUsername(paycheck.getToken());

            System.out.println(namecheck);
            Optional<UserDetail> usercheck = userDetailRepo.findById(paycheck.getUserDetailId());
            String name = usercheck.get().getUser().getEmail();
            System.out.println(name);
            Optional<Order> updateorder = orderRepository.findById(paycheck.getOrderId());
            if (namecheck.equals(usercheck.get().getUser().getEmail())) {
                updateorder.get().setPaymentStatus(1);
                orderRepository.save(updateorder.get());
                MBservice.createMybook(paycheck.getOrderId(), paycheck.getUserDetailId());
                return new ResponseEntity<>(ResultDto.builder()
                        .status(true)
                        .message("Payment paid and updated successfully")
                        .build(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(ResultDto.builder().status(false).message("Payment not be paid or fail").build(), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> deleteOrder(int orderId) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);

            if (orderOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Order not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            orderRepository.delete(orderOptional.get());

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Order deleted successfully")
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Failed to delete order")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> updateOrder(int orderId, OrderUpdateRequest orderUpdateRequest) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);

            if (orderOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Order not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            Order order = orderOptional.get();
            order.setPaymentStatus(orderUpdateRequest.getPaymentStatus());
            orderRepository.save(order);

            List<OrderDetail> existingOrderDetails = orderDetailRepository.findByOrderId(orderId);

            // Remove existing order details that are not in the new book list
            List<OrderDetail> detailsToRemove = existingOrderDetails.stream()
                    .filter(detail -> !orderUpdateRequest.getBookIds().contains(detail.getBook().getId()))
                    .collect(Collectors.toList());
            orderDetailRepository.deleteAll(detailsToRemove);

            // Add new order details
            for (int bookId : orderUpdateRequest.getBookIds()) {
                if (bookRepository.findById(bookId).isPresent()) {
                    boolean exists = existingOrderDetails.stream()
                            .anyMatch(detail -> detail.getBook().getId() == bookId);
                    if (!exists) {
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrder(order);
                        orderDetail.setBook(bookRepository.findById(bookId).get());
                        orderDetailRepository.save(orderDetail);
                    }
                } else {
                    return new ResponseEntity<>(ResultDto.builder()
                            .status(false)
                            .message("Book not found")
                            .build(), HttpStatus.NOT_FOUND);
                }
            }

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Order updated successfully")
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Failed to update order")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<List<Order>>> getOrdersByUserId(int userId) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userId);

            if (userDetailOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.<List<Order>>builder()
                        .status(false)
                        .message("User not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            List<Order> orders = userDetailOptional.get().getOrders();

            return new ResponseEntity<>(ResultDto.<List<Order>>builder()
                    .status(true)
                    .message("Success")
                    .model(orders)
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.<List<Order>>builder()
                    .status(false)
                    .message("Failed to retrieve orders")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
