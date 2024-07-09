/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.category.BookAdCreateRes;
import fpt.aptech.project4_server.service.ReadService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author macos
 */
@RestController
@RequestMapping("api/v1/read")
public class ReadController {
    @Autowired
    ReadService rs;
     @PostMapping("/initial/{index}/{mybookid}")

    public ResponseEntity<?> readInitial(@PathVariable int index,@PathVariable("mybookid") int mybookid) throws IOException {

   
         return rs.createCurrentPageInitial(index,mybookid);
           
    }
//    
    
}
