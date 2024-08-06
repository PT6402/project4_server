package fpt.aptech.project4_server.service.feature_web_service.book.book_service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.util.ResultDto;

public interface BookFeatureService {
  ResponseEntity<ResultDto<?>> getBook();

  ResponseEntity<ResultDto<?>> getTopLike();
}
