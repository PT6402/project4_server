package fpt.aptech.project4_server.controller.feature_web_controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fpt.aptech.project4_server.service.feature_web_service.book.book_service.BookFeatureService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/load-book")
@RequiredArgsConstructor
public class LoadBookController {
  private final BookFeatureService bookService;

  @GetMapping
  public ResponseEntity<?> getBook() {
    return bookService.getBook();
  }
}
