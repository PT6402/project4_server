package fpt.aptech.project4_server.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import fpt.aptech.project4_server.dto.order.OrderAdmin;
import fpt.aptech.project4_server.dto.order.OrderAndDetailDto;
import fpt.aptech.project4_server.dto.order.OrderAndDetailFlutter;
import fpt.aptech.project4_server.dto.order.OrderCreateRequest;
import fpt.aptech.project4_server.dto.order.OrderDetailDto;
import fpt.aptech.project4_server.dto.order.OrderFlutterShow;
import fpt.aptech.project4_server.dto.order.OrderUpdateRequest;
import fpt.aptech.project4_server.dto.order.PaymentCheck;
import fpt.aptech.project4_server.dto.payment.PaymentResponse;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.entities.book.Review;
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
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ImageBookRepo IBrepo;
    private final OrderDetailRepository orderDetailRepository;
    private final MailServiceImpl mailService;
    private final JwtService jservice;
    private final UserDetailRepo userDetailRepo;
    private final MyBookService MBservice;
    private final CartService cartservice;
    private final CartRepository cartRepository;
    private final PaymentService paymentService;
    private final BookRepository bookRepository;
    private final PackageReadRepository packageReadRepository;
    private final ReviewRepository reviewRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

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
            // List<OrderDetail> orderdetail=
            // orderDetailRepository.findByOrderId(paycheck.getOrderId());
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

    public ResponseEntity<ResultDto<?>> checkPaymentForFlutter(int userId, PaymentCheckForFlutter paymentCheckForFlutter) {
        try {
            // Find the user by userId
            UserDetail userDetail = userDetailRepo.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));

            // Find the order by its ID
            Optional<Order> updateOrder = orderRepository.findById(paymentCheckForFlutter.getOrderId());

            if (updateOrder.isEmpty()) {
                // If order not found, return an error response
                return new ResponseEntity<>(
                        ResultDto.builder().status(false).message("Order not found").build(),
                        HttpStatus.NOT_FOUND);
            }

            Order order = updateOrder.get();

            // Verify that the order belongs to the user
            if (order.getUserDetail().getId() != userDetail.getId()) {
                return new ResponseEntity<>(
                        ResultDto.builder().status(false).message("Order does not belong to the user").build(),
                        HttpStatus.UNAUTHORIZED);
            }

            // Set payment status to 1 (indicating payment is completed)
            order.setPaymentStatus(1);
            orderRepository.save(order);

            // Create a new book entry in user's collection (MyBook) after successful payment
            // Assume MBservice.createMybook is defined elsewhere in your service
            MBservice.createMybook(paymentCheckForFlutter.getOrderId(), userDetail.getId());

            // Send payment confirmation email
            String to = userDetail.getUser().getEmail();
            String subject = "Successful payment confirmation";
            String textBody = createOrderTextBody(order, userDetail);
            mailService.sendPlainTextEmail(to, subject, textBody);

            // Return a success response
            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Payment confirmed and updated successfully")
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            // Handle any errors during the payment confirmation process
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    public ResponseEntity<ResultDto<?>> createPaymentIntentAndHandleOrder(int userId, int cartId) {
//        try {
//            Stripe.apiKey = stripeApiKey;
//
//            // Step 1: Validate the user and retrieve their cart
//            UserDetail userDetail = userDetailRepo.findByUserId(userId)
//                    .orElseThrow(() -> new Exception("UserDetail not found"));
//
//            Optional<Cart> cartOptional = cartRepository.findById(cartId);
//            if (cartOptional.isEmpty() || cartOptional.get().getCartItems().isEmpty()) {
//                return new ResponseEntity<>(
//                        ResultDto.builder().status(false).message("Cart is empty or not found").build(),
//                        HttpStatus.BAD_REQUEST);
//            }
//
//            Cart cart = cartOptional.get();
//            List<CartItem> cartItems = cart.getCartItems();
//            List<OrderDetail> orderDetails = new ArrayList<>();
//            double totalPrice = 0.0;
//
//            // Step 2: Create a new Order
//            Order order = Order.builder()
//                    .userDetail(userDetail)
//                    .paymentStatus(0) // Initial payment status
//                    .orderDetails(orderDetails)
//                    .dateOrder(LocalDateTime.now())
//                    .build();
//
//            for (CartItem cartItem : cartItems) {
//                Book book = cartItem.getBook();
//                double price = cartItem.getPrice();
//                totalPrice += price;
//
//                OrderDetail orderDetail = new OrderDetail();
//                orderDetail.setOrder(order);
//                orderDetail.setBook(book);
//                orderDetail.setDayQuantity(cartItem.getDayQuantity());
//                orderDetail.setPrice(price);
//                orderDetail.setPackId(cartItem.getPackId());
//                orderDetails.add(orderDetail);
//            }
//
//            order.setOrderDetails(orderDetails);
//            order.setTotalPrice(totalPrice);
//            Order savedOrder = orderRepository.save(order);
//
//            // Save OrderDetails after Order has been saved
//            for (OrderDetail orderDetail : orderDetails) {
//                orderDetail.setOrder(savedOrder);
//                orderDetailRepository.save(orderDetail);
//            }
//
//            // Step 3: Create the Stripe PaymentIntent
//            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
//                    .setAmount((long) (totalPrice * 100)) // Amount in cents
//                    .setCurrency("usd")
//                    .addPaymentMethodType("card") // Specify payment method type
//                    .putMetadata("order_id", String.valueOf(savedOrder.getId())) // Attach order ID as metadata
//                    .build();
//
//            PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//            // Step 4: Clear the Cart
//            cartservice.clearCart(userId);
//
//            // Step 5: Return the PaymentIntent client secret to the frontend
//            return new ResponseEntity<>(ResultDto.builder()
//                    .status(true)
//                    .message("Payment Intent created and order initialized")
//                    .model(paymentIntent.getClientSecret())
//                    .build(), HttpStatus.OK);
//
//        } catch (StripeException e) {
//            return new ResponseEntity<>(
//                    ResultDto.builder().status(false).message("Payment error: " + e.getMessage()).build(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (Exception e) {
//            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    public ResponseEntity<ResultDto<?>> createPaymentIntentAndHandleOrder(int userId, int cartId) {
        try {
            Stripe.apiKey = stripeApiKey;

            // Step 1: Validate the user and retrieve their cart
            UserDetail userDetail = userDetailRepo.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));

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

            // Step 2: Create a new Order
            Order order = Order.builder()
                    .userDetail(userDetail)
                    .paymentStatus(0) // Initial payment status
                    .orderDetails(orderDetails)
                    .dateOrder(LocalDateTime.now())
                    .build();

            for (CartItem cartItem : cartItems) {
                Book book = cartItem.getBook();
                double price = cartItem.getPrice();
                totalPrice += price;

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setBook(book);
                orderDetail.setDayQuantity(cartItem.getDayQuantity());
                orderDetail.setPrice(price);
                orderDetail.setPackId(cartItem.getPackId());
                orderDetails.add(orderDetail);
            }

            order.setOrderDetails(orderDetails);
            order.setTotalPrice(totalPrice);
            Order savedOrder = orderRepository.save(order);

            // Save OrderDetails after Order has been saved
            for (OrderDetail orderDetail : orderDetails) {
                orderDetail.setOrder(savedOrder);
                orderDetailRepository.save(orderDetail);
            }

            // Step 3: Create the Stripe PaymentIntent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (totalPrice * 100)) // Amount in cents
                    .setCurrency("usd")
                    .addPaymentMethodType("card") // Specify payment method type
                    .putMetadata("order_id", String.valueOf(savedOrder.getId())) // Attach order ID as metadata
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Step 4: Clear the Cart
            cartservice.clearCart(userId);

            // Step 5: Return the PaymentIntent client secret and order ID to the frontend
            HashMap<String, Object> responseMap = new HashMap<>();
            responseMap.put("clientSecret", paymentIntent.getClientSecret());
            responseMap.put("orderId", savedOrder.getId());

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Payment Intent created and order initialized")
                    .model(responseMap)
                    .build(), HttpStatus.OK);

        } catch (StripeException e) {
            return new ResponseEntity<>(
                    ResultDto.builder().status(false).message("Payment error: " + e.getMessage()).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(ResultDto.builder().status(false).message(e.getMessage()).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String createOrderTextBody(Order order, UserDetail userDetail) {
        StringBuilder textBody = new StringBuilder();
        textBody.append("Successful payment\n\n");
        textBody.append("Thank you for your trust in us, ").append(userDetail.getFullname())
                .append(", Your order is paid. Please check 'MyBook' to enjoy the product.\n\n");
        textBody.append("Order Details\n");
        textBody.append("Order ID: ").append(order.getId()).append("\n");
        textBody.append("Created Date: ")
                .append(order.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
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

    public ResponseEntity<ResultDto<?>> getOrdersByUserId(int userId) {
        try {
            UserDetail userDetail = userDetailRepo.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));

            List<Order> orders = userDetail.getOrders();

            List<OrderAndDetailDto> orderDtos = orders.stream()
                    .map(order -> {
                        List<OrderDetailDto> orderDetailDtos = order.getOrderDetails().stream()
                                .map(orderDetail -> {
                                    Book book = orderDetail.getBook();
                                    HashMap<String, Object> getReview = new HashMap<>();
                                    var review = reviewRepository.findByBookIdAndUserDetailId(userDetail.getId(),
                                            book.getId()).orElse(null);
                                    var orderDetailDto = OrderDetailDto.builder()
                                            .bookId(book.getId())
                                            .bookName(book.getName())
                                            .image(getImage(book.getFilePdf()).getImage_data())
                                            .price(orderDetail.getPrice())
                                            .dayPackage(
                                                    orderDetail.getDayQuantity() != null ? orderDetail.getDayQuantity()
                                                            : 0)
                                            .build();
                                    if (review != null) {
                                        getReview.put("star", review.getRating());
                                        getReview.put("content", review.getContent());
                                        getReview.put("id", review.getId());
                                        orderDetailDto.setReview(getReview);
                                    }
                                    return orderDetailDto;
                                })
                                .toList();
                        return OrderAndDetailDto.builder().orderId(order.getId()).creatDate(order.getCreateAt())
                                .orderDetails(orderDetailDtos).paymentStatus(order.getPaymentStatus()).build();
                    })
                    .toList();
            ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(orderDtos).build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
            return ResponseEntity.badRequest().body(response);
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
                                    return OrderDetailDto.builder()
                                            .id(orderDetail.getId())
                                            .bookName(book.getName())
                                            .bookId(book.getId())
                                            .dayQuantity(
                                                    orderDetail.getDayQuantity() != null ? orderDetail.getDayQuantity()
                                                            : 0)
                                            .packId(orderDetail.getPackId())
                                            .packName(packageName)
                                            .image(fileImage)
                                            .price(orderDetail.getPrice())
                                            .build();
                                    // return new OrderDetailDto(
                                    // orderDetail.getId(),
                                    // book.getName(),
                                    // book.getId(),
                                    // , // Xử

                                    // orderDetail.getPackId(),
                                    // orderDetail.getPrice(),
                                    // packageName,
                                    // fileImage);
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
                        return OrderDetailDto.builder()
                                .id(orderDetail.getId())
                                .bookName(book.getName())
                                .bookId(book.getId())
                                .dayQuantity(
                                        orderDetail.getDayQuantity() != null ? orderDetail.getDayQuantity()
                                                : 0)
                                .packId(orderDetail.getPackId())
                                .packName(packageName)
                                .image(fileImage)
                                .price(orderDetail.getPrice())
                                .build();
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
    
    public ResponseEntity<ResultDto<?>> getOrdersByUserIdF(int userId) {
        try {
            UserDetail userDetail = userDetailRepo.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));

            List<Order> orders = userDetail.getOrders();

            List<OrderAndDetailFlutter> orderDtos = orders.stream()
                    .map(order -> {
                        List<OrderFlutterShow> orderDetailDtos = order.getOrderDetails().stream()
                                .map(orderDetail -> {
                                    Book book = orderDetail.getBook();
                                    List<Review> reviews = reviewRepository.findAllByBookIdAndUserDetailId(userDetail.getId(), book.getId());

                                    List<HashMap<String, Object>> reviewList = reviews.stream().map(review -> {
                                        HashMap<String, Object> reviewMap = new HashMap<>();
                                        reviewMap.put("star", review.getRating());
                                        reviewMap.put("content", review.getContent());
                                        reviewMap.put("id", review.getId());
                                        reviewMap.put("createDate", review.getCreateAt());
                                        return reviewMap;
                                    }).collect(Collectors.toList());

                                    return OrderFlutterShow.builder()
                                            .bookId(book.getId())
                                            .bookName(book.getName())
                                            .image(getImage(book.getFilePdf()).getImage_data())
                                            .price(orderDetail.getPrice())
                                            .dayPackage(orderDetail.getDayQuantity() != null ? orderDetail.getDayQuantity() : 0)
                                            .reviews(reviewList)
                                            .build();
                                }).collect(Collectors.toList());

                        return OrderAndDetailFlutter.builder()
                                .orderId(order.getId())
                                .creatDate(order.getCreateAt())
                                .orderDetailsFlutter(orderDetailDtos)
                                .paymentStatus(order.getPaymentStatus())
                                .build();
                    }).collect(Collectors.toList());

            ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(orderDtos).build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

}


