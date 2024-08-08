package fpt.aptech.project4_server.controller.feature_web_controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fpt.aptech.project4_server.service.feature_web_service.book.author_service.AuthorFeatureService;
import fpt.aptech.project4_server.service.feature_web_service.book.book_service.BookFeatureService;
import fpt.aptech.project4_server.service.feature_web_service.book.cate_service.CateFeatureService;
import fpt.aptech.project4_server.service.feature_web_service.book.package_service.PackageFeatureService;
import fpt.aptech.project4_server.service.feature_web_service.book.publisher_service.PublisherFeatureService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/load")
@RequiredArgsConstructor
public class LoadFirstController {
  private final CateFeatureService cateService;
  private final PackageFeatureService packageService;
  private final AuthorFeatureService authorService;
  private final PublisherFeatureService publisherService;
  private final BookFeatureService bookService;

  @GetMapping("cate")
  public ResponseEntity<?> getCate() {
    return cateService.getCategory();
  }

  @GetMapping("package")
  public ResponseEntity<?> getPackage() {
    return packageService.getPackage();
  }

  @GetMapping("author")
  public ResponseEntity<?> getAuthor() {
    return authorService.getAuthor();
  }

  @GetMapping("publisher")
  public ResponseEntity<?> getPublisher() {
    return publisherService.getPublisher();
  }

  @GetMapping("top-like")
  public ResponseEntity<?> topLike() {
    return bookService.getTopLike();
  }
}
