package fpt.aptech.project4_server.service;

import java.util.HashMap;
import java.util.stream.Collectors;

import org.hibernate.mapping.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.dto.book.BookPropertiesResultAdmin;
import fpt.aptech.project4_server.dto.book.BookResultAdmin;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.repository.AuthorRepository;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.CateRepo;
import fpt.aptech.project4_server.repository.ImageBookRepo;
import fpt.aptech.project4_server.repository.PublisherRepository;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class BookService {
  private final BookRepo bookrepo;
  private final CateRepo cateRepo;
  private final AuthorRepository authorRepo;
  private final PublisherRepository publisherRepo;
  private final ImageBookRepo imageBookRepo;

  public ResponseEntity<ResultDto<?>> getBooks() {
    try {
      var listBook = bookrepo.findAll().stream().map(item -> {
        // publisher
        HashMap<String, String> publisherMap = new HashMap<>();
        publisherMap.put("id", String.valueOf(item.getPublisher().getId()));
        publisherMap.put("name", item.getPublisher().getName());

        // author
        var authors = item.getAuthors().stream().map(c -> {
          HashMap<String, String> authorMap = new HashMap<>();
          authorMap.put("id", String.valueOf(c.getId()));
          authorMap.put("name", c.getName());
          return authorMap;
        }).collect(Collectors.toList());

        // category
        var categories = item.getCategories().stream().map(c -> {
          HashMap<String, String> categoryMap = new HashMap<>();
          categoryMap.put("id", String.valueOf(c.getId()));
          categoryMap.put("name", c.getName());
          return categoryMap;
        }).collect(Collectors.toList());

        return BookResultAdmin.builder()
            .name(item.getName())
            .price(item.getPrice())
            .image(
                getImage(item.getFilePdf())
                    .getImage_data())

            .authors(authors)
            .categories(categories)
            .rating(item.getRating())
            .publisher(
                publisherMap)
            .build();
      }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(listBook).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      // Xử lý lỗi
      ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<ResultDto<?>> getPropertiesList() {
    try {
      var categories = cateRepo.findAll().stream().map(c -> {
        HashMap<String, String> categoryMap = new HashMap<>();
        categoryMap.put("id", String.valueOf(c.getId()));
        categoryMap.put("name", c.getName());
        return categoryMap;
      }).toList();
      var authors = authorRepo.findAll().stream().map(c -> {
        HashMap<String, String> authorMap = new HashMap<>();
        authorMap.put("id", String.valueOf(c.getId()));
        authorMap.put("name", c.getName());
        return authorMap;
      }).toList();
      var publishers = publisherRepo.findAll().stream().map(c -> {
        HashMap<String, String> publisherMap = new HashMap<>();
        publisherMap.put("id", String.valueOf(c.getId()));
        publisherMap.put("name", c.getName());
        return publisherMap;
      }).toList();
      var result = BookPropertiesResultAdmin.builder()
          .authors(authors)
          .categories(categories)
          .publishers(publishers)
          .build();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(result).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      // Xử lý lỗi
      ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  private ImagesBook getImage(FilePdf file) {
    System.out.println(file.getId());
    var listIB = imageBookRepo.findAll();

    for (ImagesBook c : listIB) {
      if (c.getPdf().getId() == file.getId()) {
        if (c.isCover()) {
          return c;
        }
      }
    }
    return null;

  }
}
