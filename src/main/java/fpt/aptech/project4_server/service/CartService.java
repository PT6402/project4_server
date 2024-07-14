package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.entities.user.Cart;
import fpt.aptech.project4_server.entities.user.CartItem;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.BookRepository;
import fpt.aptech.project4_server.repository.CartItemRepository;
import fpt.aptech.project4_server.repository.CartRepository;
import fpt.aptech.project4_server.repository.PackageReadRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class CartService {

    private static final Logger logger = Logger.getLogger(CartService.class.getName());

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PackageReadRepository packageReadRepository;

    @Autowired
    private UserDetailRepo userDetailRepo;

    public ResponseEntity<ResultDto<?>> addBookToCart(int userId, int bookId) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userId);
            Optional<Book> bookOptional = bookRepository.findById(bookId);

            if (userDetailOptional.isEmpty() || bookOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("User or Book not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();
            Optional<Cart> cartOptional = cartRepository.findByUserDetailId(userId);

            Cart cart;
            if (cartOptional.isPresent()) {
                cart = cartOptional.get();
            } else {
                cart = new Cart();
                cart.setUserDetail(userDetail);
                cart.setBooks(new ArrayList<>());
                userDetail.setCart(cart);
            }

            cart.getBooks().add(bookOptional.get());
            cartRepository.save(cart);

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Book added to cart")
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            logger.severe("Failed to add book to cart: " + e.getMessage());
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Failed to add book to cart")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> removeBookFromCart(int userId, int bookId) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userId);
            Optional<Book> bookOptional = bookRepository.findById(bookId);

            if (userDetailOptional.isEmpty() || bookOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("User or Book not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();
            Cart cart = userDetail.getCart();

            if (cart == null) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Cart is empty")
                        .build(), HttpStatus.NOT_FOUND);
            }

            cart.getCartItems().removeIf(cartItem -> cartItem.getBook().getId() == bookId);
            cartRepository.save(cart);

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Book removed from cart")
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            logger.severe("Failed to remove book from cart: " + e.getMessage());
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Failed to remove book from cart")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> viewCart(int userId) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userId);

            if (userDetailOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("User not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();
            Cart cart = userDetail.getCart();

            if (cart == null) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Cart is empty")
                        .build(), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Cart retrieved successfully")
                    .model(cart)
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            logger.severe("Failed to retrieve cart: " + e.getMessage());
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Failed to retrieve cart")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> clearCart(int userId) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userId);

            if (userDetailOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("User not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();
            Cart cart = userDetail.getCart();

            if (cart == null) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Cart is already empty")
                        .build(), HttpStatus.NOT_FOUND);
            }

            cart.getCartItems().clear();
            cartRepository.save(cart);

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Cart cleared successfully")
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            logger.severe("Failed to clear cart: " + e.getMessage());
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Failed to clear cart")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
