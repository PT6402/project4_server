package fpt.aptech.project4_server.controller.user;

import fpt.aptech.project4_server.dto.cart.CartAddRes;
import fpt.aptech.project4_server.dto.cart.CartUpdate;
import fpt.aptech.project4_server.security.CurrentUser;
import fpt.aptech.project4_server.security.UserGlobal;
import fpt.aptech.project4_server.service.cart.CartService;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ResultDto<?>> viewCart(@CurrentUser UserGlobal user) {
        return cartService.viewCart(user.getId());
    }

    @PostMapping
    public ResponseEntity<ResultDto<?>> addBookToCart(@CurrentUser UserGlobal user, @RequestBody CartAddRes cartres) {
        return cartService.addBookToCart(user.getId(), cartres);
    }

    @PutMapping
    public ResponseEntity<ResultDto<?>> updateCart(@CurrentUser UserGlobal user, @RequestBody CartUpdate cartUp) {
        return cartService.updateCart(user.getId(), cartUp);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<ResultDto<?>> removeBookFromCart(@CurrentUser UserGlobal user, @PathVariable int bookId) {
        return cartService.removeBookFromCart(user.getId(), bookId);
    }

    @DeleteMapping
    public ResponseEntity<ResultDto<?>> clearCart(@CurrentUser UserGlobal user) {
        return cartService.clearCart(user.getId());
    }
}
