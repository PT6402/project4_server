package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.book.BookUserRes;
import fpt.aptech.project4_server.dto.publisher.PubCreateRes;
import fpt.aptech.project4_server.dto.publisher.PubSearch;

import fpt.aptech.project4_server.entities.book.Publisher;
import fpt.aptech.project4_server.service.PublisherService;
import fpt.aptech.project4_server.util.ResultDto;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/publisher")
public class PublisherController {

    @Autowired
    PublisherService Pservice;

    @GetMapping("/")
    public ResponseEntity<ResultDto<List<Publisher>>> getPublishers() {
        return Pservice.getPublishers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultDto<Publisher>> getPublisher(@PathVariable("id") int id) {
        return Pservice.getPublisher(id);
    }

    @PostMapping("/create")
    public ResponseEntity<ResultDto<?>> createPublisher(@ModelAttribute PubCreateRes pubCreateRes) {
        ResultDto<?> response = Pservice.createPublisher(pubCreateRes);
        HttpStatus status = response.isStatus() ? HttpStatus.OK : HttpStatus.CONFLICT;
        return new ResponseEntity<>(response, status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultDto<Publisher>> updateAuthor(@PathVariable("id") int id,
            @ModelAttribute PubCreateRes pubDetails) {
        return Pservice.updatePub(id, pubDetails);
    }

    @GetMapping("/search")
    public ResponseEntity<ResultDto<List<PubSearch>>> searchAuthor(@RequestParam("name") String name)
            throws IOException {
        return Pservice.searchByNamePub(name);
    }

    @GetMapping("/booksByPub")
    public ResponseEntity<ResultDto<?>> getBooksByAuthor(@RequestParam("pubId") int pubId) {
        return Pservice.getBooksByPubId(pubId);
    }
}
