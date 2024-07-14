package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.order.OrderCreateRequest;
import fpt.aptech.project4_server.dto.order.OrderUpdateRequest;
import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.service.OrderService;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<ResultDto<?>> createOrder(@RequestBody OrderCreateRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<ResultDto<?>> checkoutCart(@PathVariable int userId, @RequestBody List<String> packageNames) {
        return orderService.checkoutCart(userId, packageNames);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ResultDto<?>> updateOrder(@PathVariable int orderId, @RequestBody OrderUpdateRequest orderUpdateRequest) {
        return orderService.updateOrder(orderId, orderUpdateRequest);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResultDto<?>> deleteOrder(@PathVariable int orderId) {
        return orderService.deleteOrder(orderId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResultDto<List<Order>>> getOrdersByUserId(@PathVariable int userId) {
        return orderService.getOrdersByUserId(userId);
    }
}
