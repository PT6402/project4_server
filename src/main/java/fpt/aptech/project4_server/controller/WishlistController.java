/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.service.WishlistService;
import fpt.aptech.project4_server.util.ResultDto;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author macos
 */
@RestController
@RequestMapping("api/v1/wishlist")
public class WishlistController {
    
   @Autowired
   WishlistService wls;
    @PostMapping("/create")
    public ResponseEntity<?> createWishlist(@RequestParam Integer bookid,@RequestParam Integer userdetailid)throws IOException{
        return wls.createWishlist(bookid,userdetailid);
    }
 
    @GetMapping("/show/{id}")
    public ResponseEntity<?> getWishlistByUserDetailId(@PathVariable("id") int id) {
        ResponseEntity<?> response = wls.ShowWishlist(id);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    } 
     @DeleteMapping("/{id}")
    public ResponseEntity<ResultDto<Void>> deleteWishlist(@PathVariable("id") int id) {
        return wls.deleteWishlist(id);
    }
}
