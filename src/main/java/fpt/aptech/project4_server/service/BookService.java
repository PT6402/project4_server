package fpt.aptech.project4_server.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.dto.book.BookPropertiesResultAdmin;
import fpt.aptech.project4_server.dto.book.BookResultAdmin;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.AuthorRepository;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.CateRepo;
import fpt.aptech.project4_server.repository.ImageBookRepo;
import fpt.aptech.project4_server.repository.Mybookrepo;
import fpt.aptech.project4_server.repository.PublisherRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
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
  private final Mybookrepo mybookrepo;
  private final UserDetailRepo userDetailRepo;

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
            .id(item.getId())
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

  public ResponseEntity<ResultDto<?>> getMyBook(int userId) {
    try {
      UserDetail userDetail = userDetailRepo.findByUserId(userId)
          .orElseThrow(() -> new Exception("UserDetail not found"));
      List<Mybook> mybooks = mybookrepo.findByUserDetailId(userDetail.getId());
      var listMyBook = mybooks.stream()
          .map(mybook -> {
            HashMap<String, Object> myBookItem = new HashMap<>();

            myBookItem.put("id", mybook.getBook().getId());
            myBookItem.put("bookname", mybook.getBook().getName());
            myBookItem.put("dayGet", mybook.getCreateAt());
            myBookItem.put("dayExpired", mybook.getExpiredDate());
            myBookItem.put("fileimage", getImage(mybook.getBook().getFilePdf())
                .getImage_data());

            handleStateAndDayTotal(myBookItem, mybook);// return dayTotal and status
            return myBookItem;
          }).toList();
      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(listMyBook).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }

  public ResponseEntity<ResultDto<?>> getOneBookAdmin(int bookId) {
    try {
      var book = bookrepo.findById(bookId).orElseThrow(() -> new Exception("book not found"));

      int[] authorIds = book.getAuthors().stream()
          .mapToInt(author -> author.getId())
          .toArray();

      int[] categoryIds = book.getCategories().stream()
          .mapToInt(cate -> cate.getId())
          .toArray();

      HashMap<String, Object> bookResult = new HashMap<>();
      bookResult.put("name", book.getName());
      bookResult.put("price", book.getPrice());
      bookResult.put("edition", book.getEdition());
      bookResult.put("authors", authorIds);
      bookResult.put("catetorys", categoryIds);
      bookResult.put("publisherId", book.getPublisher().getId());
      bookResult.put("description", book.getDescription());

      ResultDto<?> response = ResultDto.builder().message("ok").status(true).model(bookResult).build();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      ResultDto<?> response = ResultDto.builder().message(e.getMessage()).status(false).build();
      return ResponseEntity.badRequest().body(response);
    }
  }

  private void handleStateAndDayTotal(HashMap<String, Object> myBookItem, Mybook mybook) {
    Long daysDif = mybook.getExpiredDate() != null
        ? ChronoUnit.DAYS.between(LocalDateTime.now(), mybook.getExpiredDate())
        : null;
    int caseValue = daysDif == null ? 0 : (daysDif > 3 ? 1 : (daysDif < 0 ? 3 : 2));

    switch (caseValue) {
      case 0:
        myBookItem.put("dayTotal", 0);
        myBookItem.put("status", 0);
        break;
      case 1:
        myBookItem.put("dayTotal", daysDif != null ? Math.abs(daysDif) : 0);
        myBookItem.put("status", 1);
        break;
      case 2:
        myBookItem.put("dayTotal", daysDif != null ? Math.abs(daysDif) : 0);
        myBookItem.put("status", 2);

        break;
      case 3:
        myBookItem.put("dayTotal", 0);
        myBookItem.put("status", 3);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + caseValue);
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
