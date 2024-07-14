package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import fpt.aptech.project4_server.service.CartService;
import fpt.aptech.project4_server.service.OrderService;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/add/{userId}/{bookId}")
    public ResponseEntity<ResultDto<?>> addBookToCart(@PathVariable int userId, @PathVariable int bookId) {
        return cartService.addBookToCart(userId, bookId);
    }

    @DeleteMapping("/remove/{userId}/{bookId}")
    public ResponseEntity<ResultDto<?>> removeBookFromCart(@PathVariable int userId, @PathVariable int bookId) {
        return cartService.removeBookFromCart(userId, bookId);
    }

    @GetMapping("/view/{userId}")
    public ResponseEntity<ResultDto<?>> viewCart(@PathVariable int userId) {
        return cartService.viewCart(userId);
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ResultDto<?>> clearCart(@PathVariable int userId) {
        return cartService.clearCart(userId);
    }

//    @PostMapping("/checkout/{userId}")
//    public ResponseEntity<ResultDto<?>> checkoutCart(@PathVariable int userId) {
//        return orderService.checkoutCart(userId);
//    }
}
