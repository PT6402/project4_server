/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.category.BookAdCreateRes;
import fpt.aptech.project4_server.dto.category.CateAdCreateRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.service.PdfService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author macos
 */
@RestController
@RequestMapping("api/v1/book")
public class BookController {
    @Autowired
    PdfService pv;
    
    @PostMapping("/create")

    public ResponseEntity<?> createBook(@ModelAttribute BookAdCreateRes bookad) throws IOException {

   
         return pv.createNewBook(bookad);
           
    }
//    
    @GetMapping("/showlist")
    public ResponseEntity<?> BookLUshow(){
        return pv.BooklistUserShow();
    }
     @GetMapping("/showone/{id}")
    public ResponseEntity<?> Bookshow(@PathVariable int id){
        return pv.BookSingleUserShow(id);
    }
} 
