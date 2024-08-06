package fpt.aptech.project4_server.service.feature_web_service.user.wishlist_service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.util.ResultDto;

public interface WishlistFeatureService {
  ResponseEntity<ResultDto<?>> getWishlist(int userId);
}
