/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.project4_server.controller;

import static com.fasterxml.jackson.databind.util.ClassUtil.name;
import fpt.aptech.project4_server.dto.category.CateAdCreateRes;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.service.CateService;
import jakarta.websocket.server.PathParam;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author macos
 */
@RestController
@RequestMapping("api/v1/cate")

public class CateController {

    @Autowired
    CateService categoryService;

    @PostMapping("/create")

    public ResponseEntity<?> createCategory(@ModelAttribute CateAdCreateRes cate) throws IOException {

        return categoryService.createNewCate(cate);
    }

    @GetMapping("/userShow")
    public ResponseEntity<?> Usershow() {

        return categoryService.CateUserShow();
    }

    @PutMapping("/update/{id}")

    public ResponseEntity<?> updateCategory(@PathVariable Integer id,@ModelAttribute CateAdCreateRes cate) {

        return categoryService.UpdateCate(id, cate);
    }
}
