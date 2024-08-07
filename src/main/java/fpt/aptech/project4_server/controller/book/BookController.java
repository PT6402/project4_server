package fpt.aptech.project4_server.controller.book;

import fpt.aptech.project4_server.dto.book.BookAdCreateRes;
import fpt.aptech.project4_server.dto.book.BookFilter;
import fpt.aptech.project4_server.dto.category.CateAdCreateRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.service.BookService;
import fpt.aptech.project4_server.service.PdfService;
import fpt.aptech.project4_server.util.ResultDto;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/book")
@RequiredArgsConstructor
public class BookController {

    private final PdfService pv;

    private final BookService adminBookService;

    @PostMapping("/create")

    public ResponseEntity<?> createBook(@ModelAttribute BookAdCreateRes bookad) throws IOException {

        return pv.createNewBook(bookad);

    }
    //
    //
    // @GetMapping("/showlist")
    // public ResponseEntity<?> BookLUshow() {
    // return pv.BooklistUserShow();
    // }

    @GetMapping("/showone/{id}")
    public ResponseEntity<?> Bookshow(@PathVariable int id) {
        return pv.BookSingleUserShow(id);
    }

    @GetMapping("/showpage")
    public ResponseEntity<?> BookPage(@RequestParam("page") Integer id, @RequestParam("limit") Integer limit) {
        return pv.Pagnination(id, limit);

    }

    @GetMapping("/search")
    public ResponseEntity<ResultDto<?>> BookSearch(@RequestParam("name") String name) {
        ResultDto<?> response = pv.searchByName(name);
        if (response.isStatus()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filterPrice")
    public ResponseEntity<ResultDto<?>> BookFilterPrice(@RequestParam("StaPrice") Integer StaPrice,
            @RequestParam("EndPrice") Integer EndPrice) {
        ResultDto<?> response = pv.searchByPrice(StaPrice, EndPrice);
        if (response.isStatus()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/showpage")
    public ResponseEntity<?> BookPageFilter(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit,
            @RequestBody BookFilter bookfilter) {
        return pv.Filter(page, limit, bookfilter);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBook(@ModelAttribute BookAdCreateRes bookad, @PathVariable int id) {

        return pv.UpdateBook(id, bookad);

    }

    // @DeleteMapping("/delete/{id}")
    // public ResponseEntity<ResultDto<?>> deleteBook(@PathVariable int id) {
    // ResultDto<?> response = pv.deleteBookById(id);
    // if (response.isStatus()) {
    // return new ResponseEntity<>(response, HttpStatus.OK);
    // } else {
    // return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    // }
    // }

    @GetMapping("/check/{bookid}")
    public ResponseEntity<ResultDto<?>> checkStatus(@PathVariable int bookid) {
        ResultDto<?> result = pv.checkStatus(bookid);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<ResultDto<?>> notSellBook(@PathVariable int bookId) {
        ResultDto<?> result = pv.notSellBook(bookId);
        if (result.isStatus()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/admin/books")
    public ResponseEntity<ResultDto<?>> getBookAll() {
        return adminBookService.getBooks();
    }

    // get categories - authors - publisher
    @GetMapping("/admin/book-properties")
    public ResponseEntity<ResultDto<?>> getPropertiesList() {
        return adminBookService.getPropertiesList();
    }

}
