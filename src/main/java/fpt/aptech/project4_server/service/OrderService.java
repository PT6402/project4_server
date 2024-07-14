package fpt.aptech.project4_server.service;

import com.stripe.exception.StripeException;
import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import fpt.aptech.project4_server.dto.order.OrderCreateRequest;
import fpt.aptech.project4_server.dto.order.OrderUpdateRequest;
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
    private UserDetailRepo userDetailRepo;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PackageReadRepository packageReadRepository;

    public ResponseEntity<ResultDto<?>> createOrder(OrderCreateRequest orderRequest) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(orderRequest.getUserId());
            if (userDetailOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder().status(false).message("User not found").build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();

            List<OrderDetail> orderDetails = new ArrayList<>();
            for (CartItemAddRequest cartItemRequest : orderRequest.getCartItems()) {
                Optional<Book> bookOptional = bookRepository.findById(cartItemRequest.getBookId());
                if (bookOptional.isEmpty()) {
                    return new ResponseEntity<>(ResultDto.builder().status(false).message("Book not found: " + cartItemRequest.getBookId()).build(), HttpStatus.NOT_FOUND);
                }

                Optional<PackageRead> packageOptional = packageReadRepository.findByPackageName(cartItemRequest.getPackageName());
                if (packageOptional.isEmpty()) {
                    return new ResponseEntity<>(ResultDto.builder().status(false).message("Package not found: " + cartItemRequest.getPackageName()).build(), HttpStatus.NOT_FOUND);
                }

                Book book = bookOptional.get();
                PackageRead packageRead = packageOptional.get();
                double rentPrice = calculateRentPrice(book.getPrice(), packageRead.getDayQuantity());

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setBook(book);
                orderDetail.setDayQuantity(packageRead.getDayQuantity());
                orderDetail.setRentPrice(rentPrice);
                orderDetails.add(orderDetail);
            }

            Order order = Order.builder()
                    .userDetails(List.of(userDetail))
                    .paymentStatus(0)
                    .orderDetails(orderDetails)
                    .build();

            Order savedOrder = orderRepository.save(order);

            for (OrderDetail orderDetail : orderDetails) {
                orderDetail.setOrder(savedOrder);
                orderDetailRepository.save(orderDetail);
            }

            // Create payment link
            PaymentResponse paymentResponse = paymentService.createPaymentLink(savedOrder);

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

    private double calculateRentPrice(double basePrice, int dayQuantity) {
        return (basePrice / 45) * dayQuantity;
    }

    public ResponseEntity<ResultDto<?>> checkoutCart(int userId, List<String> packageNames) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userId);
            if (userDetailOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder().status(false).message("User not found").build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();
            Cart cart = userDetail.getCart();

            if (cart == null || cart.getBooks().isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder().status(false).message("Cart is empty").build(), HttpStatus.BAD_REQUEST);
            }

            Order order = Order.builder()
                    .userDetails(List.of(userDetail))
                    .paymentStatus(0)
                    .orderDetails(new ArrayList<>())
                    .build();

            List<OrderDetail> orderDetails = new ArrayList<>();
            for (int i = 0; i < cart.getBooks().size(); i++) {
                Book book = cart.getBooks().get(i);
                String packageName = packageNames.get(i);

                Optional<PackageRead> packageOptional = packageReadRepository.findByPackageName(packageName);
                if (packageOptional.isEmpty()) {
                    return new ResponseEntity<>(ResultDto.builder().status(false).message("Package not found: " + packageName).build(), HttpStatus.NOT_FOUND);
                }

                PackageRead packageRead = packageOptional.get();
                double rentPrice = calculateRentPrice(book.getPrice(), packageRead.getDayQuantity());

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setBook(book);
                orderDetail.setDayQuantity(packageRead.getDayQuantity());
                orderDetail.setRentPrice(rentPrice);

                orderDetails.add(orderDetail);
            }

            order.setOrderDetails(orderDetails);
            Order savedOrder = orderRepository.save(order);

            // Create payment link
            PaymentResponse paymentResponse = paymentService.createPaymentLink(savedOrder);

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
