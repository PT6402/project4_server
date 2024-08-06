package fpt.aptech.project4_server.service.feature_web_service.user.wishlist_service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.repository.WishlistRepo;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistFeatureServiceImpl implements WishlistFeatureService {
  private final WishlistRepo wishlistRepo;
  private final UserDetailRepo userDetailRepo;

  @Override
  public ResponseEntity<ResultDto<?>> getWishlist(int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));
      var wishlist = wishlistRepo.findByUserDetailId(userDetail.getId()).stream().mapToInt(c -> c.getBook().getId())
          .toArray();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(wishlist).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }
}
