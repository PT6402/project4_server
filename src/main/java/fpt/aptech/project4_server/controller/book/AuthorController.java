package fpt.aptech.project4_server.controller.book;

import fpt.aptech.project4_server.dto.author.AuthorAdminCreateRes;
import fpt.aptech.project4_server.dto.author.AuthorSearch;
import fpt.aptech.project4_server.dto.book.BookUserRes;
import fpt.aptech.project4_server.entities.book.Author;
import fpt.aptech.project4_server.service.AuthorService;
import fpt.aptech.project4_server.service.PdfService;
import fpt.aptech.project4_server.util.ResultDto;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    @Autowired
    AuthorService authorService;

    @GetMapping
    public ResponseEntity<ResultDto<List<Author>>> getAuthors() {
        return authorService.getAuthors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto<Author>> getAuthor(@PathVariable("id") int id) {
        return authorService.getAuthor(id);
    }

    @PostMapping
    public ResponseEntity<ResultDto<?>> saveAuthor(@RequestParam("name") String name,
            @RequestParam("fileImage") MultipartFile fileImage) throws IOException {
        AuthorAdminCreateRes authorRes = AuthorAdminCreateRes.builder()
                .name(name)
                .fileImage(fileImage)
                .build();
        try {
            return authorService.createAuthor(authorRes);
        } catch (IOException e) {
            ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDto<Author>> updateAuthor(@PathVariable("id") int id,
            @RequestBody Author authorDetails) {
        return authorService.updateAuthor(id, authorDetails);
    }

    @GetMapping("/search")
    public ResponseEntity<ResultDto<List<AuthorSearch>>> searchAuthor(@RequestParam("name") String name)
            throws IOException {
        return authorService.searchByNameAuthor(name);
    }

    @GetMapping("/booksByAuthor")
    public ResponseEntity<ResultDto<?>> getBooksByAuthor(@RequestParam("authorId") int authorId) {
        return authorService.getBooksByAuthorId(authorId);
    }

    // @DeleteMapping("/authors/{id}")
    // public ResponseEntity<ResultDto<Void>> deleteAuthor(@PathVariable("id") int
    // id) {
    // return authorService.deleteAuthor(id);
    // }
}
