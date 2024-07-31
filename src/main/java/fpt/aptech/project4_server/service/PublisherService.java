package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.author.AuthorSearch;
import fpt.aptech.project4_server.dto.author.AuthorUserRes;
import fpt.aptech.project4_server.dto.book.BookUserRes;
import fpt.aptech.project4_server.dto.category.CateAdCreateRes;
import fpt.aptech.project4_server.dto.category.CateShow;
import fpt.aptech.project4_server.dto.publisher.PubCreateRes;
import fpt.aptech.project4_server.dto.publisher.PubSearch;
import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.book.Publisher;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.ImageBookRepo;
import fpt.aptech.project4_server.repository.PublisherRepository;
import fpt.aptech.project4_server.util.ResultDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PublisherService {
    @Autowired
    PublisherRepository Prepo;
    @Autowired
    BookRepo bookrepo;
    @Autowired
    private ImageBookRepo IBrepo;

    public ResultDto<?> createPublisher(PubCreateRes pubCreateRes) {
        try {
            List<Publisher> listcheck = Prepo.findAll();
            for (Publisher p : listcheck) {
                if (pubCreateRes.getName() != null && p.getName().equals(pubCreateRes.getName())) {
                    ResultDto<?> response = ResultDto.builder()
                            .status(false)
                            .message("Publisher already existed")
                            .build();
                    return response;
                }
            }
            byte[] imageData = pubCreateRes.getFileImage().getBytes();
            Publisher newPublisher = Publisher.builder()
                    .name(pubCreateRes.getName())
                    .description(pubCreateRes.getDescription())
                    .Image_data(imageData)
                    .build();

            Prepo.save(newPublisher);
            ResultDto<?> response = ResultDto.builder()
                    .status(true)
                    .message("Create successfully")
                    .build();
            return response;

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build();
            return response;
        }
    }

    public ResponseEntity<ResultDto<List<Publisher>>> getPublishers() {
        try {
            List<Publisher> publishers = Prepo.findAll();
            ResultDto<List<Publisher>> response = ResultDto.<List<Publisher>>builder()
                    .status(true)
                    .message("Success")
                    .model(publishers)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<List<Publisher>> response = ResultDto.<List<Publisher>>builder()
                    .status(false)
                    .message("Failed to retrieve publisher")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<Publisher>> getPublisher(int id) {
        try {
            Optional<Publisher> pubOptional = Prepo.findById(id);
            if (pubOptional.isPresent()) {
                ResultDto<Publisher> response = ResultDto.<Publisher>builder()
                        .status(true)
                        .message("Success")
                        .model(pubOptional.get())
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Publisher> response = ResultDto.<Publisher>builder()
                        .status(false)
                        .message("Publisher not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Publisher> response = ResultDto.<Publisher>builder()
                    .status(false)
                    .message("Failed to retrieve publisher")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // public ResponseEntity<ResultDto<Publisher>> updatePub(Integer id,
    // PubCreateRes pubDetails) {
    // try {
    // Optional<Publisher> pubOptional = Prepo.findById(id);
    // if (pubOptional.isPresent()) {
    // Publisher pub = pubOptional.get();
    // pub.setName(pubDetails.getName());
    // pub.setDescription(pubDetails.getDescription());
    //
    // Publisher updatedPub = Prepo.save(pub);
    // ResultDto<Publisher> response = ResultDto.<Publisher>builder()
    // .status(true)
    // .message("Publisher updated successfully")
    // .model(updatedPub)
    // .build();
    // return new ResponseEntity<>(response, HttpStatus.OK);
    // } else {
    // ResultDto<Publisher> response = ResultDto.<Publisher>builder()
    // .status(false)
    // .message("Publiser not found")
    // .build();
    // return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    // }
    // } catch (Exception e) {
    // ResultDto<Publisher> response = ResultDto.<Publisher>builder()
    // .status(false)
    // .message("Failed to update publisher")
    // .build();
    // return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    // }
    // }

    public ResponseEntity<ResultDto<Publisher>> updatePub(Integer id, PubCreateRes pubDetails) {
        try {
            Optional<Publisher> pubOptional = Prepo.findById(id);
            if (pubOptional.isPresent()) {
                Publisher pub = pubOptional.get();
                pub.setName(pubDetails.getName());
                pub.setDescription(pubDetails.getDescription());
                if (pubDetails.getFileImage() != null && !pubDetails.getFileImage().isEmpty()) {
                    pub.setImage_data(pubDetails.getFileImage().getBytes());
                }
                Publisher updatedPub = Prepo.save(pub);
                ResultDto<Publisher> response = ResultDto.<Publisher>builder()
                        .status(true)
                        .message("Publisher updated successfully")
                        .model(updatedPub)
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Publisher> response = ResultDto.<Publisher>builder()
                        .status(false)
                        .message("Publisher not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Publisher> response = ResultDto.<Publisher>builder()
                    .status(false)
                    .message("Failed to update publisher")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResultDto<List<PubSearch>>> searchByNamePub(String wordSearch) {
        try {
            List<Publisher> pubs = Prepo.searchByNamePub(wordSearch);
            List<PubSearch> PubSearchList = pubs.stream()
                    .map(pub -> {
                        PubSearch dto = new PubSearch();
                        dto.setId(pub.getId());
                        dto.setFileImage(pub.getImage_data()); // Giả sử `getImageData` trả về byte[]
                        dto.setName(pub.getName());
                        return dto;
                    })
                    .collect(Collectors.toList());

            ResultDto<List<PubSearch>> response = ResultDto.<List<PubSearch>>builder()
                    .status(true)
                    .message("ok")
                    .model(PubSearchList)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<List<PubSearch>> response = ResultDto.<List<PubSearch>>builder()
                    .status(false)
                    .message("Failed to retrieve publisher")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<ResultDto<?>> getBooksByPubId(int pubId) {
        try {
            Publisher publisher = Prepo.findById(pubId).orElseThrow(() -> new Exception("publisher not found"));

            var bookUserResList = publisher.getBooks().stream()
                    .map(c -> {
                        HashMap<String, Object> pubMap = new HashMap<>();
                        pubMap.put("id", c.getId());
                        pubMap.put("name", c.getName());
                        pubMap.put("price", c.getPrice());
                        pubMap.put("image", getImage(c.getFilePdf()).getImage_data());
                        return pubMap;
                    })
                    .toList();

            HashMap<String, Object> result = new HashMap<>();
            result.put("listBook", bookUserResList);
            result.put("image", publisher.getImage_data());
            result.put("name", publisher.getName());
            result.put("description", publisher.getDescription());

            ResultDto<?> response = ResultDto.builder()
                    .status(true)
                    .message("Successfully retrieved books by publisher ID")
                    .model(result)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ResultDto<List<BookUserRes>> response = ResultDto.<List<BookUserRes>>builder()
                    .status(false)
                    .message("Failed to retrieve books by publisher ID")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
                .description(book.getDescription())
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
