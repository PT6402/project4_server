/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.mybook.MBUserRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.CurrentPage;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.entities.book.Review;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.entities.user.Order;
import fpt.aptech.project4_server.entities.user.OrderDetail;
import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.CPRepo;
import fpt.aptech.project4_server.repository.Mybookrepo;
import fpt.aptech.project4_server.repository.OrderRepository;
import fpt.aptech.project4_server.repository.PackageReadRepository;
import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    OrderRepository orderrepo;
    @Autowired
    UserDetailRepo UDrepo;
    @Autowired
    BookRepo Brepo;
    @Autowired
    CPRepo CPrepo;
    @Autowired
    PackageReadRepository PRrepo;

    private static final Logger logger = LoggerFactory.getLogger(MyBookService.class);

    public ResponseEntity<ResultDto<?>> createMybook(int orderId, int userDetailId) {
        try {
            // Lấy UserDetail từ userDetailId
            UserDetail userDetail = UDrepo.findById(userDetailId)
                    .orElseThrow(() -> new IllegalArgumentException("UserDetail not found"));

            // Lấy Order từ orderId
            Order order = orderrepo.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            // Lặp qua từng OrderDetail trong Order và xử lý từng sách
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                Book book = orderDetail.getBook();

                // Lấy packageReadId từ OrderDetail
                Integer packageReadId = orderDetail.getPackId();
                System.out.println("heloooooo" + packageReadId);

                if (packageReadId != null && packageReadId > 0) {
                    // Truy xuất PackageRead từ packageReadId
                    PackageRead packageRead = PRrepo.findById(packageReadId).get();

                    // Tính toán expired_date
                    LocalDateTime createAt = LocalDateTime.now();
                    LocalDateTime expiredDate = createAt.plusDays(packageRead.getDayQuantity());
                    CurrentPage currentPage = new CurrentPage();
                    currentPage.setCurrenPageIndex(0);
                    currentPage.setImagePageData(book.getFilePdf().getFile_data()); // Assuming this field exists
                    CPrepo.save(currentPage);

                    // Tạo Mybook mới và lưu vào cơ sở dữ liệu
                    Mybook mybook = Mybook.builder()
                            .userDetail(userDetail)
                            .book(book)
                            .currentpage(currentPage)
                            .createAt(LocalDateTime.now())
                            .ExpiredDate(expiredDate)
                            .build();

                    MBrepo.save(mybook);
                } else {
                    LocalDateTime expiredDate = null;
                    CurrentPage currentPage = new CurrentPage();
                    currentPage.setCurrenPageIndex(0);
                    currentPage.setImagePageData(book.getFilePdf().getFile_data()); // Assuming this field exists
                    CPrepo.save(currentPage);

                    // Tạo Mybook mới và lưu vào cơ sở dữ liệu
                    Mybook mybook = Mybook.builder()
                            .userDetail(userDetail)
                            .book(book)
                            .currentpage(currentPage)
                            .createAt(LocalDateTime.now())
                            .ExpiredDate(expiredDate)
                            .build();

                    MBrepo.save(mybook);
                }

            }

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

    // show list mybook
    public ResponseEntity<ResultDto<?>> ShowMybooklist(int userId) {
        try {
            UserDetail userDetail = UDrepo.findByUserId(userId)
                    .orElseThrow(() -> new Exception("UserDetail not found"));
            List<Mybook> mybooks = MBrepo.findByUserDetailId(userDetail.getId());

            List<MBUserRes> mbUserResList = mybooks.stream()
                    .map(mybook -> {
                        MBUserRes mbUserRes = new MBUserRes();
                        mbUserRes.setBookname(mybook.getBook().getName());
                        mbUserRes.setBookid(mybook.getBook().getId());
                        mbUserRes.setMybookid(mybook.getId());
                        mbUserRes.setExpiredDate(mybook.getExpiredDate());

                        Long daysDif = mybook.getExpiredDate() != null
                                ? ChronoUnit.DAYS.between(LocalDateTime.now(), mybook.getExpiredDate())
                                : null;

                        if (mybook.getExpiredDate() == null) {
                            mbUserRes.setDays(0);
                            mbUserRes.setStatus(0);
                        } else if (daysDif > 3) {
                            mbUserRes.setDays((int) Math.abs(daysDif));
                            mbUserRes.setStatus(1);
                        } else if (daysDif < 0) {
                            mbUserRes.setDays(0);
                            mbUserRes.setStatus(3);
                        } else {
                            mbUserRes.setDays((int) Math.abs(daysDif));
                            mbUserRes.setStatus(2);
                        }

                        byte[] coverImage = mybook.getBook().getFilePdf().getImagesbook().stream()
                                .filter(c -> c.isCover())
                                .map(ImagesBook::getImage_data)
                                .findFirst()
                                .orElse(null);

                        mbUserRes.setFileImage(coverImage);

                        return mbUserRes;
                    })
                    .collect(Collectors.toList());

            ResultDto<List<MBUserRes>> response = ResultDto.<List<MBUserRes>>builder()
                    .status(true)
                    .message("Success")
                    .model(mbUserResList)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error fetching Mybooks for userDetailId {}: {}", userId, e.getMessage(), e);
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message("Failed to fetch Mybooks: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
