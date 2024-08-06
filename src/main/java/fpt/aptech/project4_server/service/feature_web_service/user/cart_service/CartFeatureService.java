package fpt.aptech.project4_server.service.feature_web_service.user.cart_service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.util.ResultDto;

public interface CartFeatureService {
  ResponseEntity<ResultDto<?>> getCart(int userId);
}
