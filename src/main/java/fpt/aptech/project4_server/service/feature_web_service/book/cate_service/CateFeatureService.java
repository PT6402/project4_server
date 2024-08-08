package fpt.aptech.project4_server.service.feature_web_service.book.cate_service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.util.ResultDto;

public interface CateFeatureService {
  ResponseEntity<ResultDto<?>> getCategory();
}
