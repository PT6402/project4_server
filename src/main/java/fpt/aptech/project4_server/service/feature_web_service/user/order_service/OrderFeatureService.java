package fpt.aptech.project4_server.service.feature_web_service.user.order_service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.util.ResultDto;

public interface OrderFeatureService {
  ResponseEntity<ResultDto<?>> getOrder(int userId);
}
