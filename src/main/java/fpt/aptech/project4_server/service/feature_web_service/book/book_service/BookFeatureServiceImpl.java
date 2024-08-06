package fpt.aptech.project4_server.service.feature_web_service.book.book_service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.user.Wishlist;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.WishlistRepo;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookFeatureServiceImpl implements BookFeatureService {
  private final BookRepo bookRepo;
  private final WishlistRepo wishlistRepo;

  @Override
  public ResponseEntity<ResultDto<?>> getBook() {
    try {
      var books = bookRepo.findAll().stream().map(c -> {
        HashMap<String, Object> bookMap = new HashMap<>();
        bookMap.put("id", c.getId());
        bookMap.put("name", c.getName());
        bookMap.put("image", c.getImageCover());
        bookMap.put("price", c.getPrice());
        bookMap.put("rating", c.getRating());
        return bookMap;
      }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(books).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }

  @Override
  public ResponseEntity<ResultDto<?>> getTopLike() {
    try {

      List<Wishlist> wishlists = wishlistRepo.findAll();

      var topLike = wishlists.stream()
          .map(Wishlist::getBook)
          .filter(Objects::nonNull)
          .collect(Collectors.groupingBy(Book::getId, Collectors.counting()))
          .entrySet().stream()
          .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
          .limit(3)
          .mapToInt(Map.Entry::getKey)
          .toArray();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(topLike).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }

  }

}
