package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.book.BookCart;
import fpt.aptech.project4_server.dto.book.BookSearch;
import fpt.aptech.project4_server.dto.cart.CartAddRes;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    private PackageReadRepository Prepo;

    public ResponseEntity<ResultDto<?>> addBookToCart(int userdetailId, CartAddRes cartres) {
        try {
            Optional<UserDetail> userDetailOptional = userDetailRepo.findById(userdetailId);
            Optional<Book> bookOptional = bookRepository.findById(cartres.getBookId());

            if (userDetailOptional.isEmpty() || bookOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("User or Book not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            UserDetail userDetail = userDetailOptional.get();

            boolean bookExistsInMybook = userDetailOptional.get().getMybook().stream()
                    .anyMatch(mybook -> mybook.getBook().getId() == cartres.getBookId());

            if (bookExistsInMybook) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Book already exists in your collection")
                        .build(), HttpStatus.BAD_REQUEST);
            }

            Optional<Cart> cartOptional = cartRepository.findByUserDetailId(userdetailId);

            Cart cart;
            if (cartOptional.isPresent()) {
                cart = cartOptional.get();
            } else {
                cart = new Cart();
                cart.setUserDetail(userDetail);
                cart.setBooks(new ArrayList<>());
                userDetail.setCart(cart);
            }
            boolean bookExistsInCart = cart.getCartItems().stream()
                    .anyMatch(item -> item.getBook().getId() == cartres.getBookId());

            if (bookExistsInCart) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Book already exists in cart")
                        .build(), HttpStatus.BAD_REQUEST);
            }
            CartItem item = new CartItem();
            item.setBook(bookOptional.get());
            item.setCart(cart);

            BigDecimal price = BigDecimal.valueOf(bookOptional.get().getPrice());
            if (cartres.getIbuy() == true) {
                item.setPrice(price.doubleValue());
                Optional<PackageRead> PackageOptional = Prepo.findById(cartres.getPackId());
                item.setPackageName(null);
                item.setDayQuantity(null);
            } else {
                Optional<PackageRead> PackageOptional = Prepo.findById(cartres.getPackId());
                List<PackageRead> packageReadList = Prepo.findAll();
                int maxDayQuantity = packageReadList.stream()
                        .mapToInt(PackageRead::getDayQuantity)
                        .max()
                        .orElse(1);
                double rentPrice = price.divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(maxDayQuantity), 5, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(PackageOptional.get().getDayQuantity()))
                        .setScale(0, RoundingMode.HALF_UP)
                        .doubleValue();
                item.setPrice(rentPrice);

                item.setPackageName(PackageOptional.get().getPackageName());
                item.setDayQuantity(PackageOptional.get().getDayQuantity());
            }

            cart.getCartItems().add(item); // Thêm cart item vào danh sách cart items của cart
            cartRepository.save(cart);

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    // .model(cart)
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

            // Tìm và xóa CartItem có bookId từ danh sách CartItems của Cart
            List<CartItem> cartItems = cart.getCartItems();
            Optional<CartItem> cartItemOptional = cartItems.stream()
                    .filter(item -> item.getBook().getId() == bookId)
                    .findFirst();

            if (cartItemOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Book not found in cart")
                        .build(), HttpStatus.NOT_FOUND);
            }

            // Xóa CartItem khỏi danh sách CartItems của Cart
            cartItems.remove(cartItemOptional.get());

            // Lưu lại Cart sau khi xóa
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

    //
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
            var cart = cartRepository.findByUserDetailId(userDetail.getId()).orElse(null);
            if (cart == null) {
                throw new Exception("oh no");
            }
            // Cart cart = userDetail.getCart();
            List<BookSearch> list = cart.getCartItems().stream()
                    .map(c -> BookSearch.builder().bookid(c.getBook().getId()).build())
                    .toList();
            // if (cart == null) {
            // return new ResponseEntity<>(ResultDto.builder()
            // .status(false)
            // .message("Cart is empty")
            // .build(), HttpStatus.NOT_FOUND);
            // }

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Cart retrieved successfully")
                    .model(list)
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            logger.severe("Failed to retrieve cart: " + e.getMessage());
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Failed to retrieve cart")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> clearCart(int cartId) {
        try {
            Optional<Cart> cartOptional = cartRepository.findById(cartId);

            if (cartOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Cart not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            Cart cart = cartOptional.get();
            List<CartItem> cartItems = cart.getCartItems();

            if (cartItems.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Cart is already empty")
                        .build(), HttpStatus.NOT_FOUND);
            }

            // Xóa tất cả CartItem trong danh sách CartItems của Cart
            cartItems.clear();

            // Lưu lại Cart sau khi xóa
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