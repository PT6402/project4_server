package fpt.aptech.project4_server.service;

import com.stripe.exception.StripeException;
import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import fpt.aptech.project4_server.dto.order.OrderAdmin;
import fpt.aptech.project4_server.dto.order.OrderAndDetailDto;
import fpt.aptech.project4_server.dto.order.OrderCreateRequest;
import fpt.aptech.project4_server.dto.order.OrderDetailDto;
import fpt.aptech.project4_server.dto.order.OrderUpdateRequest;
import fpt.aptech.project4_server.dto.order.PaymentCheck;
import fpt.aptech.project4_server.dto.payment.PaymentResponse;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.entities.user.Cart;
import fpt.aptech.project4_server.entities.user.CartItem;
import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.entities.user.OrderDetail;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.*;
import fpt.aptech.project4_server.service.cart.CartService;
import fpt.aptech.project4_server.service.jwt.JwtService;
import fpt.aptech.project4_server.service.mail.MailServiceImpl;
import fpt.aptech.project4_server.util.ResultDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private ImageBookRepo IBrepo;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private MailServiceImpl mailService;
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
            UserDetail userDetail = userDetailRepo.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));

            // Lấy Cart dựa vào cartId
            Optional<Cart> cartOptional = cartRepository.findById(cartId);
            if (cartOptional.isEmpty() || cartOptional.get().getCartItems().isEmpty()) {
                return new ResponseEntity<>(
                        ResultDto.builder().status(false).message("Cart is empty or not found").build(),
                        HttpStatus.BAD_REQUEST);
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
            cartservice.clearCart(userId);
            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Order created successfully")
                    .model(paymentResponse)
                    .build(), HttpStatus.CREATED);
        } catch (StripeException e) {
            return new ResponseEntity<>(
                    ResultDto.builder().status(false).message("Payment error: " + e.getMessage()).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> checkPayment(int userId, PaymentCheck paycheck) {
        try {
            UserDetail userDetail = userDetailRepo.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));
//            List<OrderDetail> orderdetail= orderDetailRepository.findByOrderId(paycheck.getOrderId());
            int userIdToken = jservice.getUserIdByToken(paycheck.getToken());
            Optional<Order> updateorder = orderRepository.findById(paycheck.getOrderId());
            Order order = updateorder.get();
            if (userIdToken == userId) {
                updateorder.get().setPaymentStatus(1);
                orderRepository.save(order);
                MBservice.createMybook(paycheck.getOrderId(), userDetail.getId());

                String to = userDetail.getUser().getEmail();
                String subject = "Successfully payment confirmation";
                String textBody = createOrderTextBody(order, userDetail);
                mailService.sendPlainTextEmail(to, subject, textBody);

                return new ResponseEntity<>(ResultDto.builder()
                        .status(true)
                        .message("Payment paid and updated successfully")
                        .build(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(
                        ResultDto.builder().status(false).message("Payment not be paid or fail").build(),
                        HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private String createOrderTextBody(Order order, UserDetail userDetail) {
        StringBuilder textBody = new StringBuilder();
        textBody.append("Successful payment\n\n");
        textBody.append("Thank you for your trust in us, ").append(userDetail.getFullname()).append(", Your order is paid. Please check 'MyBook' to enjoy the product.\n\n");
        textBody.append("Order Details\n");
        textBody.append("Order ID: ").append(order.getId()).append("\n");
        textBody.append("Created Date: ").append(order.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        textBody.append("-----------------------------------------------------\n");
        textBody.append("Book Name\t\tPrice\n");
        textBody.append("-----------------------------------------------------\n");

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());
        for (OrderDetail item : orderDetails) {
            textBody.append(item.getBook().getName()).append("\t\t").append(item.getPrice()).append("\n");
        }

        textBody.append("-----------------------------------------------------\n");
        textBody.append("Total: ").append(order.getTotalPrice()).append("\n");

        return textBody.toString();
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

    public ImagesBook getImage(FilePdf file) {
        System.out.println(file.getId());
        var listIB = IBrepo.findAll();

        for (ImagesBook c : listIB) {
            if (c.getPdf().getId() == file.getId()) {
                if (c.isCover()) {
                    return c;
                }
            }
        }
        return null;

    }

    public ResponseEntity<ResultDto<List<OrderAndDetailDto>>> getOrdersByUserId(int userId) {
        try {
            UserDetail userDetail = userDetailRepo.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));

            List<Order> orders = userDetail.getOrders();

            List<OrderAndDetailDto> orderDtos = orders.stream()
                    .map(order -> {
                        List<OrderDetailDto> orderDetailDtos = order.getOrderDetails().stream()
                                .map(orderDetail -> {
                                    Book book = orderDetail.getBook();
                                    ImagesBook image = getImage(book.getFilePdf());
                                    byte[] fileImage = image != null ? image.getImage_data() : null;
                                    String packageName = "";
                                    Optional<PackageRead> pack = packageReadRepository
                                            .findById(orderDetail.getPackId());
                                    if (pack.isPresent()) {
                                        packageName = pack.get().getPackageName();
                                    }
                                    return new OrderDetailDto(
                                            orderDetail.getId(),
                                            book.getName(),
                                            book.getId(),
                                            orderDetail.getDayQuantity() != null ? orderDetail.getDayQuantity() : 0, // Xử
                                            // lý
                                            // giá
                                            // trị
                                            // null
                                            orderDetail.getPackId(),
                                            orderDetail.getPrice(),
                                            packageName,
                                            fileImage);
                                })
                                .collect(Collectors.toList());
                        return new OrderAndDetailDto(order.getId(), order.getCreateAt(), orderDetailDtos,
                                order.getPaymentStatus());
                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(ResultDto.<List<OrderAndDetailDto>>builder()
                    .status(true)
                    .message("Success")
                    .model(orderDtos)
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để kiểm tra
            return new ResponseEntity<>(ResultDto.<List<OrderAndDetailDto>>builder()
                    .status(false)
                    .message("Failed to retrieve orders")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<List<OrderAdmin>>> getOrdersAdmin() {
        try {
            // Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userId);

            List<Order> orders = orderRepository.findAll();

            List<OrderAdmin> orderDtos = orders.stream()
                    .map(order -> {
                        List<OrderDetailDto> orderDetailDtos = order.getOrderDetails().stream()
                                .map(orderDetail -> {
                                    Book book = orderDetail.getBook();
                                    ImagesBook image = getImage(book.getFilePdf());
                                    byte[] fileImage = image != null ? image.getImage_data() : null;
                                    String packageName = "";
                                    Optional<PackageRead> pack = packageReadRepository
                                            .findById(orderDetail.getPackId());
                                    if (pack.isPresent()) {
                                        packageName = pack.get().getPackageName();
                                    }
                                    return new OrderDetailDto(
                                            orderDetail.getId(),
                                            book.getName(),
                                            book.getId(),
                                            orderDetail.getDayQuantity() != null ? orderDetail.getDayQuantity() : 0, // Xử

                                            orderDetail.getPackId(),
                                            orderDetail.getPrice(),
                                            packageName,
                                            fileImage);
                                })
                                .collect(Collectors.toList());
                        return new OrderAdmin(order.getId(), order.getCreateAt(), orderDetailDtos,
                                order.getPaymentStatus(), order.getUserDetail().getFullname(),
                                order.getUserDetail().getUser().getEmail());

                    })
                    .collect(Collectors.toList());

            return new ResponseEntity<>(ResultDto.<List<OrderAdmin>>builder()
                    .status(true)
                    .message("Success")
                    .model(orderDtos)
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để kiểm tra
            return new ResponseEntity<>(ResultDto.<List<OrderAdmin>>builder()
                    .status(false)
                    .message("Failed to retrieve orders")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<OrderAdmin>> getOrderDetailsForAdmin(int orderId) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);

            if (orderOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.<OrderAdmin>builder()
                        .status(false)
                        .message("Order not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            Order order = orderOptional.get();
            List<OrderDetailDto> orderDetailDtos = order.getOrderDetails().stream()
                    .map(orderDetail -> {
                        Book book = orderDetail.getBook();
                        ImagesBook image = getImage(book.getFilePdf());
                        byte[] fileImage = image != null ? image.getImage_data() : null;
                        String packageName = "";
                        Optional<PackageRead> pack = packageReadRepository
                                .findById(orderDetail.getPackId());
                        if (pack.isPresent()) {
                            packageName = pack.get().getPackageName();
                        }
                        return new OrderDetailDto(
                                orderDetail.getId(),
                                book.getName(),
                                book.getId(),
                                orderDetail.getDayQuantity() != null ? orderDetail.getDayQuantity() : 0,
                                orderDetail.getPackId(),
                                orderDetail.getPrice(),
                                packageName,
                                fileImage);
                    })
                    .collect(Collectors.toList());

            OrderAdmin orderAdmin = new OrderAdmin(
                    order.getId(),
                    order.getCreateAt(),
                    orderDetailDtos,
                    order.getPaymentStatus(),
                    order.getUserDetail().getFullname(),
                    order.getUserDetail().getUser().getEmail());

            return new ResponseEntity<>(ResultDto.<OrderAdmin>builder()
                    .status(true)
                    .message("Success")
                    .model(orderAdmin)
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(ResultDto.<OrderAdmin>builder()
                    .status(false)
                    .message("Failed to retrieve order details")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
