package fpt.aptech.project4_server.controller.user;

import fpt.aptech.project4_server.security.CurrentUser;
import fpt.aptech.project4_server.security.UserGlobal;
import fpt.aptech.project4_server.service.wishlist.WishlistService;
import fpt.aptech.project4_server.util.ResultDto;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/wishlist")
public class WishlistController {

    @Autowired
    WishlistService wls;

    @PostMapping
    public ResponseEntity<?> createWishlist(@RequestParam Integer bookid, @CurrentUser UserGlobal user)
            throws IOException {
        return wls.createWishlist(bookid, user.getId());
    }

    @GetMapping
    public ResponseEntity<?> getWishlistByUserDetailId(@CurrentUser UserGlobal user) {
        ResponseEntity<?> response = wls.ShowWishlist(user.getId());
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<ResultDto<?>> deleteWishlist(@PathVariable("bookId") int bookId,
                                                       @CurrentUser UserGlobal user) {
        return wls.deleteWishlist(bookId, user.getId());
    }


    @GetMapping("/check")
    public ResponseEntity<ResultDto<?>> checkStatus(
            @RequestParam("bookId") int bookId,
            @CurrentUser UserGlobal user) {
        System.out.println("bookid:" + bookId + "useriD:" + user.getId());
        return wls.checkStatus(bookId, user.getId());
    }
}
