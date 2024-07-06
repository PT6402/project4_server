package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.order.OrderCreateRequest;
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

    @PutMapping("/{orderId}")
    public ResponseEntity<ResultDto<?>> updateOrder(@PathVariable int orderId, @RequestParam int paymentStatus, @RequestBody List<Integer> bookIds) {
        return orderService.updateOrder(orderId, paymentStatus, bookIds);
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
