package fpt.aptech.project4_server.controller.user;

import fpt.aptech.project4_server.dto.order.OrderAdmin;
import fpt.aptech.project4_server.dto.order.OrderAndDetailDto;
import fpt.aptech.project4_server.dto.order.OrderCreateRequest;
import fpt.aptech.project4_server.dto.order.OrderDetailDto;
import fpt.aptech.project4_server.dto.order.OrderUpdateRequest;
import fpt.aptech.project4_server.dto.order.PaymentCheck;
import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.security.CurrentUser;
import fpt.aptech.project4_server.security.UserGlobal;
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

    @PostMapping("/check")
    public ResponseEntity<ResultDto<?>> checkpayment(@CurrentUser UserGlobal user, @RequestBody PaymentCheck paycheck) {
        return orderService.checkPayment(user.getId(), paycheck);
    }

    @PostMapping("/checkout/{cartId}")
    public ResponseEntity<ResultDto<?>> checkoutCart(@CurrentUser UserGlobal user, @PathVariable int cartId) {
        return orderService.checkoutCart(user.getId(), cartId);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ResultDto<?>> updateOrder(@PathVariable int orderId,
            @RequestBody OrderUpdateRequest orderUpdateRequest) {
        return orderService.updateOrder(orderId, orderUpdateRequest);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ResultDto<?>> deleteOrder(@PathVariable int orderId) {
        return orderService.deleteOrder(orderId);
    }

    @GetMapping
    public ResponseEntity<ResultDto<List<OrderAndDetailDto>>> getOrdersByUserId(@CurrentUser UserGlobal user) {
        return orderService.getOrdersByUserId(user.getId());
    }

    @GetMapping("/admin")
    public ResponseEntity<ResultDto<List<OrderAdmin>>> getOrdersAdmin() {
        return orderService.getOrdersAdmin();
    }

    @GetMapping("/admin/{orderId}")
    public ResponseEntity<ResultDto<OrderAdmin>> getOrderDetailsForAdmin(@PathVariable int orderId) {
        return orderService.getOrderDetailsForAdmin(orderId);
    }

}