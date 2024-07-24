package fpt.aptech.project4_server.service.cart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.dto.cart.CartAddRes;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final BookRepository bookRepository;
  private final PackageReadRepository packageReadRepository;
  private final UserDetailRepo userDetailRepo;
  private final PackageReadRepository Prepo;

  public ResponseEntity<ResultDto<?>> addBookToCart(int userId, CartAddRes cartres) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));

      Book book = bookRepository.findById(cartres.getBookId())
          .orElseThrow(() -> new Exception("Book not found"));

      var cart = cartRepository.findByUserDetailId(userDetail.getId())
          .orElse(Cart.builder().userDetail(userDetail).cartItems(new ArrayList<>()).build());
      if (cart.getCartItems().isEmpty()) {
        userDetail.setCart(cart);
      }

      boolean bookExistsInMybook = userDetail.getMybook().stream()
          .anyMatch(mybook -> mybook.getBook().getId() == cartres.getBookId());

      if (bookExistsInMybook) {
        throw new Exception("Book already exists in your collection");
      }

      boolean bookExistsInCart = cart.getCartItems().stream()
          .anyMatch(item -> item.getBook().getId() == cartres.getBookId());

      if (bookExistsInCart) {
        throw new Exception("Book already exists in cart");
      }

      CartItem item = new CartItem();
      item.setBook(book);
      item.setCart(cart);

      BigDecimal price = BigDecimal.valueOf(book.getPrice());
      if (cartres.getIbuy()) {
        item.setPrice(price.doubleValue());
        item.setPackageName(null);
        item.setDayQuantity(null);
        item.setPackId(0);
        item.setIbuy(cartres.getIbuy());
      } else {
        PackageRead packageRead = Prepo.findById(cartres.getPackId()).orElseThrow(() -> new Exception(
            "Package not found"));

        List<PackageRead> packageReadList = Prepo.findAll();
        int maxDayQuantity = packageReadList.stream()
            .mapToInt(PackageRead::getDayQuantity)
            .max()
            .orElse(1);
        double rentPrice = price.divide(BigDecimal.valueOf(2), 5, RoundingMode.HALF_UP)
            .divide(BigDecimal.valueOf(maxDayQuantity), 5, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(packageRead.getDayQuantity())).setScale(0, RoundingMode.HALF_UP)
            .doubleValue();
        item.setPrice(rentPrice);
        item.setPackId(cartres.getPackId());
        item.setPackageName(packageRead.getPackageName());
        item.setIbuy(cartres.getIbuy());
        item.setDayQuantity(packageRead.getDayQuantity());
      }

      cart.getCartItems().add(item); // Thêm cart item vào danh sách cart items của cart
      cartRepository.save(cart);

      return new ResponseEntity<>(ResultDto.builder()
          .status(true)
          .message("Book added to cart")
          .build(), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(ResultDto.builder()
          .status(false)
          .message(e.getMessage())
          .build(), HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<ResultDto<?>> removeBookFromCart(int userId, int bookId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));

      Cart cart = userDetail.getCart();

      if (cart == null) {
        throw new Exception("cart is empty");
      }

      // Tìm và xóa CartItem có bookId từ danh sách CartItems của Cart
      List<CartItem> cartItems = cart.getCartItems();
      Optional<CartItem> cartItemOptional = cartItems.stream()
          .filter(item -> item.getBook().getId() == bookId)
          .findFirst();

      if (cartItemOptional.isEmpty()) {
        throw new Exception("Book not found in cart");
      }

      cartItems.remove(cartItemOptional.get());
      cartRepository.save(cart);

      return new ResponseEntity<>(ResultDto.builder()
          .status(true)
          .message("Book removed from cart")
          .build(), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(ResultDto.builder()
          .status(false)
          .message(e.getMessage())
          .build(), HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<ResultDto<?>> viewCart(int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));

      var cart = cartRepository.findByUserDetailId(userDetail.getId())
          .orElseThrow(() -> new Exception("cart not found"));

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
                  .multiply(BigDecimal.valueOf(packageRead.getDayQuantity()))
                  .setScale(0, RoundingMode.HALF_UP)
                  .doubleValue();

              return new PackageShowbook(
                  packageRead.getId(),
                  packageRead.getPackageName(),
                  packageRead.getDayQuantity(),
                  rentPrice);
            })
            .collect(Collectors.toList());

        return CartItemShow.builder()
            .cartId(item.getCart().getId())
            .cartItemId(item.getId())
            .bookId(item.getBook().getId())
            .bookName(item.getBook().getName())
            .packId(item.getPackId())
            .ibuy(item.getIbuy())
            .priceBuy(item.getBook().getPrice())
            .packlist(packageList)
            .imageData(item.getBook().getFilePdf().getImagesbook().get(0).getImage_data())
            .build();
      }).toList();

      return new ResponseEntity<>(ResultDto.builder()
          .status(true)
          .message("cart empty")
          .model(cartItems)
          .build(), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(ResultDto.builder()
          .status(false)
          .message(e.getMessage())
          .build(), HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<ResultDto<?>> clearCart(int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));

      var cart = cartRepository.findByUserDetailId(userDetail.getId())
          .orElseThrow(() -> new Exception("cart not found"));

      List<CartItem> cartItems = cart.getCartItems();

      if (cartItems.isEmpty()) {
        throw new Exception("cart is already empty");
      }
      cartItems.clear();
      cartRepository.save(cart);

      return new ResponseEntity<>(ResultDto.builder()
          .status(true)
          .message("Cart cleared successfully")
          .build(), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(ResultDto.builder()
          .status(false)
          .message(e.getMessage())
          .build(), HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<ResultDto<?>> updateCart(int userId, CartUpdate cartUp) {
    try {
      var cartItem = cartItemRepository.findById(cartUp.getCartItemId())
          .orElseThrow(() -> new Exception("cart item not found"));

      if (cartUp.getPackId() == 0) {
        cartItem.setIbuy(Boolean.TRUE);
        cartItem.setPrice(cartItem.getBook().getPrice());
        cartItem.setDayQuantity(null);
        cartItem.setPackageName(null);
        cartItem.setPackId(0);
      } else {
        var pack = packageReadRepository.findById(cartUp.getPackId())
            .orElseThrow(() -> new Exception("package not found"));

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
      cartItemRepository.save(cartItem);

      return new ResponseEntity<>(ResultDto.builder()
          .status(true)
          .message("Cart Item be Updated Successfully")
          .build(), HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(ResultDto.builder()
          .status(false)
          .message(e.getMessage())
          .build(), HttpStatus.BAD_REQUEST);
    }
  }
}
