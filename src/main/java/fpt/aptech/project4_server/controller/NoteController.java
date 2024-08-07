/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Controller.java to edit this template
 */
package fpt.aptech.project4_server.controller;

import fpt.aptech.project4_server.dto.note.NoteUserCreateRes;
import fpt.aptech.project4_server.service.NotePageService;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author macos
 */
@RestController
@RequestMapping("api/v1/note")
public class NoteController {
@Autowired
NotePageService nps;
@PostMapping("/create/{mybookid}")
public ResponseEntity<?> createNote(@PathVariable Integer mybookid, @ModelAttribute NoteUserCreateRes noteres) throws IOException
{
    return nps.createNotePage(mybookid, noteres);
}
   
    

}
