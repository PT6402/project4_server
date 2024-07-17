package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.cart.CartAddRes;
import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import fpt.aptech.project4_server.dto.cart.CartUpdate;
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

    @PostMapping("/add/{userdetailId}")
    public ResponseEntity<ResultDto<?>> addBookToCart(@PathVariable int userdetailId,@RequestBody CartAddRes cartres) {
        return cartService.addBookToCart(userdetailId, cartres);
    }

    @DeleteMapping("/remove/{userId}/{bookId}")
    public ResponseEntity<ResultDto<?>> removeBookFromCart(@PathVariable int userId, @PathVariable int bookId) {
        return cartService.removeBookFromCart(userId, bookId);
    }

//    @GetMapping("/view/{userId}")
//    public ResponseEntity<ResultDto<?>> viewCart(@PathVariable int userId) {
//        return cartService.viewCart(userId);
//    }
    @GetMapping("/view")
    public ResponseEntity<ResultDto<?>> viewCart(@RequestParam int userId) {
        return cartService.viewCart(userId);
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<ResultDto<?>> clearCart(@PathVariable int userId) {
        return cartService.clearCart(userId);
    }

    @PutMapping("/update")
    public ResponseEntity<ResultDto<?>> updateCart(@RequestBody CartUpdate cartUp) {
        return cartService.updateCart(cartUp);
    }
}
