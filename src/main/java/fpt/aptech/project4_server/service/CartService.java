package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.book.BookCart;
import fpt.aptech.project4_server.dto.book.BookSearch;
import fpt.aptech.project4_server.dto.cart.CartAddRes;
import fpt.aptech.project4_server.dto.cart.CartItemAddRequest;
import fpt.aptech.project4_server.dto.cart.CartItemShow;
import fpt.aptech.project4_server.dto.cart.CartUpdate;
import fpt.aptech.project4_server.dto.packageread.PackageShowbook;
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
import java.util.OptionalDouble;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
                item.setPackId(0);

                item.setIbuy(cartres.getIbuy());
            } else {
                Optional<PackageRead> PackageOptional = Prepo.findById(cartres.getPackId());
                List<PackageRead> packageReadList = Prepo.findAll();
                int maxDayQuantity = packageReadList.stream()
                        .mapToInt(PackageRead::getDayQuantity)
                        .max()
                        .orElse(1);
                double rentPrice = price.divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(maxDayQuantity), 5, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(PackageOptional.get().getDayQuantity())).setScale(0, RoundingMode.HALF_UP)
                        .doubleValue();
                item.setPrice(rentPrice);
                item.setPackId(cartres.getPackId());
                item.setPackageName(PackageOptional.get().getPackageName());
                item.setIbuy(cartres.getIbuy());
                item.setDayQuantity(PackageOptional.get().getDayQuantity());
            }

            cart.getCartItems().add(item); // Thêm cart item vào danh sách cart items của cart
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
            Optional<Cart> cartOptional = cartRepository.findByUserDetailId(userDetail.getId());

            if (cartOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Cart not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            Cart cart = cartOptional.get();
            List<CartItemShow> cartItems = cart.getCartItems().stream().map(item -> {

                List<PackageShowbook> packageList = Prepo.findAll().stream()
                        .map(packageRead -> {
                            BigDecimal price = BigDecimal.valueOf(item.getBook().getPrice());

                            int maxDayQuantity = Prepo.findAll().stream()
                                    .mapToInt(PackageRead::getDayQuantity)
                                    .max()
                                    .orElse(1);

                            double rentPrice = price.divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP)
                                    .divide(BigDecimal.valueOf(maxDayQuantity), 5, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(packageRead.getDayQuantity())).setScale(0, RoundingMode.HALF_UP)
                                    .doubleValue();

                            return new PackageShowbook(
                                    packageRead.getId(),
                                    packageRead.getPackageName(),
                                    packageRead.getDayQuantity(),
                                    rentPrice
                            );
                        })
                        .collect(Collectors.toList());

                return CartItemShow.builder()
                        .cartItemId(item.getId())
                        .bookId(item.getBook().getId())
                        .bookName(item.getBook().getName())
                        .packId(item.getPackId())
                        .ibuy(item.getIbuy())
                        .priceBuy(item.getBook().getPrice())
                        .packlist(packageList)
                        .build();
            }).toList();

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("cart empty")
                    .model(cartItems)
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            logger.severe(e.getMessage());
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("get cart fail")
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

    public ResponseEntity<ResultDto<?>> updateCart(CartUpdate cartUp) {
        try {
            Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartUp.getCartItemId());

            if (cartItemOptional.isEmpty()) {
                return new ResponseEntity<>(ResultDto.builder()
                        .status(false)
                        .message("Cart not found")
                        .build(), HttpStatus.NOT_FOUND);
            }

            CartItem cartItem = cartItemOptional.get();

          
            if (cartUp.getPackId() == 0) {
                cartItem.setIbuy(Boolean.TRUE);
                cartItem.setPrice(cartItem.getBook().getPrice());
                cartItem.setDayQuantity(null); 
                cartItem.setPackageName(null); 
                cartItem.setPackId(0);
            } else {
                Optional<PackageRead> packOptional = packageReadRepository.findById(cartUp.getPackId());

                if (packOptional.isEmpty()) {
                    return new ResponseEntity<>(ResultDto.builder()
                            .status(false)
                            .message("Package not found")
                            .build(), HttpStatus.NOT_FOUND);
                }

                PackageRead pack = packOptional.get();
                BigDecimal price = BigDecimal.valueOf(cartItem.getBook().getPrice());

                // Tính toán giá thuê sách dựa trên thông tin gói
                int maxDayQuantity = Prepo.findAll().stream()
                        .mapToInt(PackageRead::getDayQuantity)
                        .max()
                        .orElse(1);

                double rentPrice = price.divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(maxDayQuantity), 5, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(pack.getDayQuantity())).setScale(0, RoundingMode.HALF_UP)
                        .doubleValue();
                cartItem.setPackId(cartUp.getPackId());
                cartItem.setIbuy(Boolean.FALSE);
                cartItem.setPrice(rentPrice);
                cartItem.setDayQuantity(pack.getDayQuantity());
                cartItem.setPackageName(pack.getPackageName());
            }

            // Lưu thay đổi vào cơ sở dữ liệu
            cartItemRepository.save(cartItem);

            return new ResponseEntity<>(ResultDto.builder()
                    .status(true)
                    .message("Cart Item be Updated Successfully")
                    .build(), HttpStatus.OK);

        } catch (Exception e) {
            logger.severe("Error: " + e.getMessage());
            return new ResponseEntity<>(ResultDto.builder()
                    .status(false)
                    .message("Updated Fail")
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
