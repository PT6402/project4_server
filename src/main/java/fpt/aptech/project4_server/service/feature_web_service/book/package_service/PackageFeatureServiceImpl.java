package fpt.aptech.project4_server.service.feature_web_service.book.package_service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.repository.PackageReadRepository;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PackageFeatureServiceImpl implements PackageFeatureService {
  private final PackageReadRepository packageRepository;

  @Override
  public ResponseEntity<ResultDto<?>> getPackage() {
    try {
      var packages = packageRepository.findAll().stream().map(c -> {
        HashMap<String, Object> packMap = new HashMap<>();
        packMap.put("id", c.getId());
        packMap.put("name", c.getPackageName());
        packMap.put("day", c.getDayQuantity());
        return packMap;
      }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(packages).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }

}
