/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.mybook.MBUserRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.book.Review;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.Mybookrepo;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author macos
 */
@Service
public class MyBookService {

    @Autowired
    Mybookrepo MBrepo;
    @Autowired
    UserDetailRepo UDrepo;
    @Autowired
    BookRepo Brepo;

    public ResponseEntity<ResultDto<?>> createMybook(int bookid, int userdetailid) {
        try {
            UserDetail userDetail = UDrepo.findById(userdetailid)
                    .orElseThrow(() -> new IllegalArgumentException("UserDetail not found"));

            Book book = Brepo.findById(bookid)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
            System.out.println(book.getId());
            // Kiểm tra trùng lặp sách trong Mybook của UserDetail
            Optional<Mybook> existingMybook = MBrepo.findByUserDetailAndBook(userDetail.getId(), book.getId());
            if (existingMybook.isPresent()) {
                ResultDto<?> response = ResultDto.builder()
                        .status(false)
                        .message("Book already associated with Mybook")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            // Tạo Mybook mới và lưu vào cơ sở dữ liệu
            Mybook mybook = Mybook.builder()
                    .userDetail(userDetail)
                    .book(book)
                    .build();

            MBrepo.save(mybook);

            ResultDto<?> response = ResultDto.builder()
                    .status(true)
                    .message("Create successfully")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //show list mybook
    public ResponseEntity<ResultDto<?>> ShowMybooklist(int userdetailid) {
        try {
            Optional<UserDetail> optionalUD = UDrepo.findById(userdetailid);
            if (optionalUD.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResultDto.builder()
                                .status(false)
                                .message("UserDetail not found")
                                .build());
            }
            List<Mybook> mybooks = MBrepo.findByUserDetailId(userdetailid);

//       ResultDto<List<Mybook>> response = ResultDto.<List<Mybook>>builder()
//                    .status(true)
//                    .message("Success")
//                    .model(mybooks)
//                    .build();
//            return new ResponseEntity<>(response, HttpStatus.OK);
            List<MBUserRes> mbUserResList = mybooks.stream()
                    .map(mybook -> {
                        MBUserRes mbUserRes = new MBUserRes();
                        mbUserRes.setBookname(mybook.getBook().getName());
                        mbUserRes.setBookid(mybook.getBook().getId());
                        mbUserRes.setBookAuthor(mybook.getBook().getAuthors().stream().findFirst().orElse(null).getName()); // Assuming one author per book for simplicity

                        // Lấy hình ảnh từ danh sách imagebook có cover = 1
                        byte[] coverImage = mybook.getBook().getFilePdf().getImagesbook().stream()
                                .filter(c -> c.isCover())
                                .map(c -> c.getImage_data())
                                .findFirst()
                                .orElse(null);

                        mbUserRes.setFileImage(coverImage);

                        return mbUserRes;
                    })
                    .toList();

            ResultDto<List<MBUserRes>> response = ResultDto.<List<MBUserRes>>builder()
                    .status(true)
                    .message("Success")
                    .model(mbUserResList)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message("Failed to fetch Mybooks: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
