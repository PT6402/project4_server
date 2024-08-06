package fpt.aptech.project4_server.service.feature_web_service.book.cate_service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.repository.CateRepo;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CateFeatureServiceImpl implements CateFeatureService {
  private final CateRepo cateRepo;

  @Override
  public ResponseEntity<ResultDto<?>> getCategory() {
    try {
      var categories = cateRepo.findAll().stream().map(c -> {
        HashMap<String, Object> cateMap = new HashMap<>();
        cateMap.put("id", c.getId());
        cateMap.put("name", c.getName());
        cateMap.put("image", c.getImagedata());
        cateMap.put("description", c.getDescription());
        return cateMap;
      }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(categories).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }

}
