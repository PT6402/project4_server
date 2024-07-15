package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.author.AuthorAdminCreateRes;
import fpt.aptech.project4_server.dto.author.AuthorSearch;
import fpt.aptech.project4_server.dto.author.AuthorShow;
import fpt.aptech.project4_server.dto.author.AuthorUserRes;
import fpt.aptech.project4_server.dto.book.BookUserRes;
import fpt.aptech.project4_server.dto.book.BooklistUserRes;
import fpt.aptech.project4_server.dto.category.CateShow;
import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.repository.AuthorRepository;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.ImageBookRepo;
import fpt.aptech.project4_server.util.ResultDto;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthorService {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepo bookrepo;
    @Autowired
    private ImageBookRepo IBrepo;

    @Value("${upload.path}")
    private String fileUpload;

    public ResponseEntity<ResultDto<List<Author>>> getAuthors() {
        try {
            List<Author> authors = authorRepository.findAll();
            ResultDto<List<Author>> response = ResultDto.<List<Author>>builder()
                    .status(true)
                    .message("Success")
                    .model(authors)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<List<Author>> response = ResultDto.<List<Author>>builder()
                    .status(false)
                    .message("Failed to retrieve authors")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<Author>> getAuthor(int id) {
        try {
            Optional<Author> authorOptional = authorRepository.findById(id);
            if (authorOptional.isPresent()) {
                ResultDto<Author> response = ResultDto.<Author>builder()
                        .status(true)
                        .message("Success")
                        .model(authorOptional.get())
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Author> response = ResultDto.<Author>builder()
                        .status(false)
                        .message("Author not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Author> response = ResultDto.<Author>builder()
                    .status(false)
                    .message("Failed to retrieve author")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<List<AuthorSearch>>> searchByNameAuthor(String wordSearch) {
        try {
            List<Author> authors = authorRepository.searchByNameAuthor(wordSearch);
            List<AuthorSearch> authorSearchList = authors.stream()
                    .map(author -> {
                        AuthorSearch dto = new AuthorSearch();
                        dto.setId(author.getId());
                        dto.setFileImage(author.getImage_data()); // Giả sử `getImageData` trả về byte[]
                        dto.setName(author.getName());
                        return dto;
                    })
                    .collect(Collectors.toList());

            ResultDto<List<AuthorSearch>> response = ResultDto.<List<AuthorSearch>>builder()
                    .status(true)
                    .message("ok")
                    .model(authorSearchList)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<List<AuthorSearch>> response = ResultDto.<List<AuthorSearch>>builder()
                    .status(false)
                    .message("Failed to retrieve author")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> createAuthor(AuthorAdminCreateRes authorRes) throws IOException {
        try {
            var listcheck = authorRepository.findAll();
            for (Author a : listcheck) {
                if (a.getName().equals(authorRes.getName())) {
                    ResultDto<?> response = ResultDto.builder().status(false).message("Author already existed").build();
                    return new ResponseEntity<>(response, HttpStatus.CONFLICT);

                }
            }

            // Lưu file ảnh dưới dạng byte array
            byte[] imageData = authorRes.getFileImage().getBytes();

            var newAuthor = Author.builder()
                    .name(authorRes.getName())
                    .Image_data(imageData)
                    .build();

            authorRepository.save(newAuthor);
            ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IOException e) {
            ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResultDto<Author>> updateAuthor(Integer id, Author authorDetails) {
        try {
            Optional<Author> authorOptional = authorRepository.findById(id);
            if (authorOptional.isPresent()) {
                Author author = authorOptional.get();
                author.setName(authorDetails.getName());

                Author updatedAuthor = authorRepository.save(author);
                ResultDto<Author> response = ResultDto.<Author>builder()
                        .status(true)
                        .message("Author updated successfully")
                        .model(updatedAuthor)
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Author> response = ResultDto.<Author>builder()
                        .status(false)
                        .message("Author not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Author> response = ResultDto.<Author>builder()
                    .status(false)
                    .message("Failed to update author")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResultDto<Void>> deleteAuthor(int id) {
        try {
            Optional<Author> authorOptional = authorRepository.findById(id);
            if (authorOptional.isPresent()) {
                authorRepository.deleteById(id);
                ResultDto<Void> response = ResultDto.<Void>builder()
                        .status(true)
                        .message("Author deleted successfully")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Void> response = ResultDto.<Void>builder()
                        .status(false)
                        .message("Author not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Void> response = ResultDto.<Void>builder()
                    .status(false)
                    .message("Failed to delete author")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   public ResponseEntity<ResultDto<List<BookUserRes>>> getBooksByAuthorId(int authorId) {
    try {
        List<Book> books = bookrepo.findByAuthorId(authorId);

        List<BookUserRes> bookUserResList = books.stream()
                .map(this::convertToBookUserRes)
                .collect(Collectors.toList());

        ResultDto<List<BookUserRes>> response = ResultDto.<List<BookUserRes>>builder()
                .status(true)
                .message("Successfully retrieved books by author ID")
                .model(bookUserResList)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        ResultDto<List<BookUserRes>> response = ResultDto.<List<BookUserRes>>builder()
                .status(false)
                .message("Failed to retrieve books by author ID")
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

   private BookUserRes convertToBookUserRes(Book book) {
    ImagesBook coverImage = getImage(book.getFilePdf());
    byte[] fileImage = coverImage != null ? coverImage.getImage_data() : null;

    List<CateShow> categoryResList = book.getCategories().stream()
            .map(category -> new CateShow(category.getId(), category.getName()))
            .collect(Collectors.toList());

    List<AuthorUserRes> authorResList = book.getAuthors().stream()
            .map(author -> new AuthorUserRes(author.getId(), author.getName()))
            .collect(Collectors.toList());

 


    return BookUserRes.builder()
            .id(book.getId())
            .name(book.getName())
            .pageQuantity(book.getPageQuantity())
          
            .edition(book.getEdition())
            .publisherDescription(book.getPublisherDescription())
            .rating(book.getRating())
            .ratingQuantity(book.getRatingQuantity())
            .fileimage(fileImage)
            .catelist(categoryResList)
            .authorlist(authorResList)
          
            .build();
}

    public ImagesBook getImage(FilePdf file) {
        System.out.println(file.getId());
        var listIB = IBrepo.findAll();

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
