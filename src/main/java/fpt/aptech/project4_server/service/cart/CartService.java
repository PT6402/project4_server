package fpt.aptech.project4_server.service.cart;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.dto.cart.CartAddRes;
import fpt.aptech.project4_server.dto.cart.CartUpdate;
import fpt.aptech.project4_server.util.ResultDto;

public interface CartService {
    ResponseEntity<ResultDto<?>> viewCart(int userId);

    ResponseEntity<ResultDto<?>> addBookToCart(int userId, CartAddRes cartres);

    ResponseEntity<ResultDto<?>> updateCart(int userId, CartUpdate cartUp);

    ResponseEntity<ResultDto<?>> removeBookFromCart(int userId, int bookId);

    ResponseEntity<ResultDto<?>> clearCart(int userId);

}