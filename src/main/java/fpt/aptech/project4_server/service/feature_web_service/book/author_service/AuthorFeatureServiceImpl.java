package fpt.aptech.project4_server.service.feature_web_service.book.author_service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.repository.AuthorRepository;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorFeatureServiceImpl implements AuthorFeatureService {
  private final AuthorRepository authorRepository;

  @Override
  public ResponseEntity<ResultDto<?>> getAuthor() {
    try {
      var authors = authorRepository.findAll().stream().map(c -> {
        HashMap<String, Object> authorMap = new HashMap<>();
        authorMap.put("id", c.getId());
        authorMap.put("name", c.getName());
        authorMap.put("image", c.getImage_data());
        return authorMap;
      }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(authors).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }
}
