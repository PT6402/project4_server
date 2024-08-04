package fpt.aptech.project4_server.service.wishlist;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import fpt.aptech.project4_server.util.ResultDto;

/**
 *
 * @author macos
 */
@Service
public interface WishlistService {
    ResponseEntity<ResultDto<?>> ShowWishlist(int userId);

    ResponseEntity<ResultDto<?>> createWishlist(int bookId, int userId);

    ResponseEntity<ResultDto<?>> deleteWishlist(int bookId, int userId);
    ResponseEntity<ResultDto<?>> checkStatus(int bookId, int userId);
}
