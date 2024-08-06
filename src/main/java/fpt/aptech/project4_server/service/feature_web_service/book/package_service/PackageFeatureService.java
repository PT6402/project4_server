package fpt.aptech.project4_server.service.feature_web_service.book.package_service;

import org.springframework.http.ResponseEntity;

import fpt.aptech.project4_server.util.ResultDto;

public interface PackageFeatureService {
  ResponseEntity<ResultDto<?>> getPackage();
}
