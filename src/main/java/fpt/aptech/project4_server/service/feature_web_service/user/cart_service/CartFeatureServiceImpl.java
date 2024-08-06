package fpt.aptech.project4_server.service.feature_web_service.user.cart_service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.CartRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartFeatureServiceImpl implements CartFeatureService {
  private final CartRepository cartRepository;
  private final UserDetailRepo userDetailRepo;

  @Override
  public ResponseEntity<ResultDto<?>> getCart(int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));
      var cart = cartRepository.findByUserDetailId(userDetail.getId());

      if (cart.isEmpty()) {
        ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(new int[0]).build();
        return ResponseEntity.ok(response);
      }
      var carts = cart.get().getCartItems().stream().map(i -> {
        HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("id", i.getId());
        cartMap.put("bookId", i.getBook().getId());
        cartMap.put("packId", i.getPackId());
        return cartMap;
      }).toList();

      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(carts).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }

}
