/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.note.NoteUserCreateRes;
import fpt.aptech.project4_server.entities.book.NotePage;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.repository.BookRepo;
import fpt.aptech.project4_server.repository.Mybookrepo;
import fpt.aptech.project4_server.repository.NotePageRepository;
import fpt.aptech.project4_server.util.ResultDto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author macos
 */
@Service
public class NotePageService {
    @Autowired
    NotePageRepository NPrepo;
    @Autowired
    BookRepo Brepo;
    @Autowired
    Mybookrepo MBrepo;
    
    
    public ResponseEntity<ResultDto<?>> createNotePage(int mybookid, NoteUserCreateRes noteres) throws IOException {
    Optional<Mybook> optionalMB = MBrepo.findById(mybookid);
    if (optionalMB.isEmpty()) {
        throw new IllegalArgumentException("Mybook not found with id: " + mybookid);
    }

    Mybook existingMB = optionalMB.get();

    try (PDDocument document = Loader.loadPDF(existingMB.getBook().getFilePdf().getFile_data())) {
        if (existingMB.getCurrentpage().getCurrenPageIndex() < 0 || existingMB.getCurrentpage().getCurrenPageIndex() >= document.getNumberOfPages()) {
            throw new IllegalArgumentException("Invalid page index: " + existingMB.getCurrentpage().getCurrenPageIndex());
        }

        PDFRenderer pdfRenderer = new PDFRenderer(document);

        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(existingMB.getCurrentpage().getCurrenPageIndex(), 300);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        String imageName = existingMB.getBook().getId() + "_page_" + (existingMB.getCurrentpage().getCurrenPageIndex() + 1) + ".jpg";

        NotePage note = new NotePage();
        note.setNoteContent(noteres.getNoteContent());
        note.setNotePageData(imageInByte);
        note.setOrderPage(existingMB.getCurrentpage().getCurrenPageIndex());
        note.setMybook(existingMB);

        NPrepo.save(note);

        ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace();
        ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}


    
}
