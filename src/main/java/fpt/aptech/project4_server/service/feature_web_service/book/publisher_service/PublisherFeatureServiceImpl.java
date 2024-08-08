package fpt.aptech.project4_server.service.feature_web_service.book.publisher_service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.repository.PublisherRepository;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublisherFeatureServiceImpl implements PublisherFeatureService {
  private final PublisherRepository publisherRepository;

  @Override
  public ResponseEntity<ResultDto<?>> getPublisher() {
    try {
      var publishers = publisherRepository.findAll().stream().map(c -> {
        HashMap<String, Object> pubMap = new HashMap<>();
        pubMap.put("id", c.getId());
        pubMap.put("name", c.getName());
        pubMap.put("image", c.getImage_data());
        pubMap.put("description", c.getDescription());
        return pubMap;
      }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(publishers).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }

}
