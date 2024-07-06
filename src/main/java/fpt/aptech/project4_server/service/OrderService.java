package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.order.OrderCreateRequest;
import fpt.aptech.project4_server.dto.order.OrderUpdateRequest;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.entities.user.OrderDetail;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.BookRepository;
import fpt.aptech.project4_server.repository.OrderDetailRepository;
import fpt.aptech.project4_server.repository.OrderRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
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
    private BookRepository bookRepository;

    public ResponseEntity<ResultDto<?>> createOrder(OrderCreateRequest orderRequest) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(orderRequest.getUserId());
            if (userDetailOptional.isEmpty()) {
                ResultDto<?> response = ResultDto.builder().status(false).message("User not found").build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();

            List<Book> books = new ArrayList();

            orderRequest.getBookIds().forEach(c -> {
                var bookId = bookRepository.findById(c.getId()).get();
                books.add(bookId);
            });

            if (books.size() != orderRequest.getBookIds().size()) {
                ResultDto<?> response = ResultDto.builder().status(false).message("Some books not found").build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Order order = Order.builder()
                    .userDetails(List.of(userDetail))
                    .paymentStatus(0) // Set default payment status, adjust as needed
                    .build();

            Order savedOrder = orderRepository.save(order);
            List<OrderDetail> orderDetails = new ArrayList();
            for (Book book : books) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(savedOrder);
                orderDetail.setBook(book);

                orderDetails.add(orderDetail);
                System.out.println(book.getId());
                orderDetailRepository.save(orderDetail);
            }

            ResultDto<?> response = ResultDto.builder().status(true).message("Order created successfully").build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
            System.out.println(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
