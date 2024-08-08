package fpt.aptech.project4_server.service.feature_web_service.user.my_book_service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.util.ResultDto;

public interface MyBookFeatureService {
  ResponseEntity<ResultDto<?>> getMyBook(int userId);
}
