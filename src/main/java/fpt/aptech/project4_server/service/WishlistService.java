/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.mybook.MBUserRes;
import fpt.aptech.project4_server.dto.wishlist.WLUserRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Review;

import fpt.aptech.project4_server.entities.user.UserDetail;
import fpt.aptech.project4_server.entities.user.Wishlist;
import fpt.aptech.project4_server.repository.BookRepo;

import fpt.aptech.project4_server.repository.UserDetailRepo;
import fpt.aptech.project4_server.repository.WishlistRepo;
import fpt.aptech.project4_server.util.ResultDto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author macos
 */
@Service
public class WishlistService {

    @Autowired
    WishlistRepo WLrepo;
    @Autowired
    UserDetailRepo UDrepo;
    @Autowired
    BookRepo Brepo;

    public ResponseEntity<ResultDto<?>> createWishlist(int bookid, int userdetailid) {
        try {
            UserDetail userDetail = UDrepo.findById(userdetailid)
                    .orElseThrow(() -> new IllegalArgumentException("UserDetail not found"));

            Book book = Brepo.findById(bookid)
                    .orElseThrow(() -> new IllegalArgumentException("Book not found"));
            System.out.println(book.getId());
            // Kiểm tra trùng lặp sách trong Mybook của UserDetail
            Optional<Wishlist> existingWishlist = WLrepo.findByUserDetailAndBook(userDetail.getId(), book.getId());
            if (existingWishlist.isPresent()) {
                ResultDto<?> response = ResultDto.builder()
                        .status(false)
                        .message("Book already associated with Wishlist")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            // Tạo Mybook mới và lưu vào cơ sở dữ liệu
            Wishlist wishlist = Wishlist.builder()
                    .userDetail(userDetail)
                    .book(book)
                    .build();

            WLrepo.save(wishlist);

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
    public ResponseEntity<ResultDto<?>> ShowWishlist(int userdetailid) {
        try {
            Optional<UserDetail> optionalUD = UDrepo.findById(userdetailid);
            if (optionalUD.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResultDto.builder()
                                .status(false)
                                .message("UserDetail not found")
                                .build());
            }
            List<Wishlist> wishlists=WLrepo.findByUserDetailId(userdetailid);


            List<WLUserRes> wlUserResList = wishlists.stream()
                    .map(wishlist -> {
                        WLUserRes wlUserRes = new WLUserRes();
                        wlUserRes.setBookname(wishlist.getBook().getName());
                        wlUserRes.setBookid(wishlist.getBook().getId());
                        wlUserRes.setBookAuthor(wishlist.getBook().getAuthors().stream().findFirst().orElse(null).getName()); // Assuming one author per book for simplicity

                        // Lấy hình ảnh từ danh sách imagebook có cover = 1
                        byte[] coverImage = wishlist.getBook().getFilePdf().getImagesbook().stream()
                                .filter(c -> c.isCover())
                                .map(c -> c.getImage_data())
                                .findFirst()
                                .orElse(null);

                        wlUserRes.setFileImage(coverImage);

                        return wlUserRes;
                    })
                    .toList();

            ResultDto<List<WLUserRes>> response = ResultDto.<List<WLUserRes>>builder()
                    .status(true)
                    .message("Success")
                    .model(wlUserResList)
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
    
    public ResponseEntity<ResultDto<Void>> deleteWishlist(int id) {
        try {
            Optional<Wishlist> wishlistOptional = WLrepo.findById(id);
            if (wishlistOptional.isPresent()) {
                WLrepo.deleteById(id);

                ResultDto<Void> response = ResultDto.<Void>builder()
                        .status(true)
                        .message("Wishlist deleted successfully")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                ResultDto<Void> response = ResultDto.<Void>builder()
                        .status(false)
                        .message("Review not found")
                        .build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            ResultDto<Void> response = ResultDto.<Void>builder()
                    .status(false)
                    .message("Failed to delete wishlist")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
