package fpt.aptech.project4_server.service.wishlist;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.dto.wishlist.WLUserRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.entities.user.Wishlist;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.repository.WishlistRepo;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {
  private final UserDetailRepo userDetailRepo;
  private final WishlistRepo wishlistRepo;
  private final BookRepo bookRepo;

  public ResponseEntity<ResultDto<?>> createWishlist(int bookId, int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));

      Book book = bookRepo.findById(bookId)
          .orElseThrow(() -> new Exception("Book not found"));

      // check duplicate
      Optional<Wishlist> existingWishlist = wishlistRepo.findByUserDetailAndBook(userDetail.getId(), book.getId());

      if (existingWishlist.isPresent()) {
        throw new Exception("Book already associated with Wishlist");
      }

      wishlistRepo.save(
          Wishlist.builder()
              .userDetail(userDetail)
              .book(book)
              .build());

      ResultDto<?> response = ResultDto.builder()
          .status(true)
          .message("Create successfully")
          .build();
      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder()
          .status(false)
          .message(e.getMessage())
          .build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<ResultDto<?>> ShowWishlist(int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));

      List<Wishlist> wishlists = wishlistRepo.findByUserDetailId(userDetail.getId());

      List<WLUserRes> wlUserResList = wishlists.stream()
          .map(wishlist -> {
            WLUserRes wlUserRes = new WLUserRes();
            wlUserRes.setBookname(wishlist.getBook().getName());
            wlUserRes.setBookid(wishlist.getBook().getId());
            wlUserRes.setRating(wishlist.getBook().getRating());
            wlUserRes.setRatingQuantity(wishlist.getBook().getRatingQuantity());
            wlUserRes.setWishId(wishlist.getId());
            // Lấy hình ảnh từ danh sách imagebook có cover = 1
            byte[] coverImage = wishlist.getBook().getFilePdf().getImagesbook().stream()
                .filter(c -> c.isCover())
                .map(c -> c.getImage_data())
                .findFirst()
                .orElse(null);

            wlUserRes.setFileImage(coverImage);

            return wlUserRes;
          })
          .toList();

      ResultDto<List<WLUserRes>> response = ResultDto.<List<WLUserRes>>builder()
          .status(true)
          .message("Success")
          .model(wlUserResList)
          .build();
      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder()
          .status(false)
          .message(e.getMessage())
          .build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<ResultDto<?>> deleteWishlist(int bookId, int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));

      Book book = bookRepo.findById(bookId)
          .orElseThrow(() -> new Exception("Book not found"));

      Wishlist wishlistItem = wishlistRepo.findByUserDetailAndBook(userDetail.getId(), book.getId())
          .orElseThrow(() -> new Exception("Wishlist item not found"));
      wishlistRepo.deleteById(wishlistItem.getId());

      ResultDto<?> response = ResultDto.<Void>builder()
          .status(true)
          .message("Wishlist deleted successfully")
          .build();
      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder()
          .status(false)
          .message(e.getMessage())
          .build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }
}
