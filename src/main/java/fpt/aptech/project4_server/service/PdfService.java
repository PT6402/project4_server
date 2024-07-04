/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.category.BookAdCreateRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.repository.*;
import fpt.aptech.project4_server.util.ResultDto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author macos
 */
@Service
public class PdfService {

    @Autowired
    private PdfRepo pdfrepo;
    @Autowired
    private BookRepo bookrepo;
     @Autowired
    private CateRepo caterepo;
    @Value("${upload.path}")
    private String fileUpload;

    public FilePdf uploadAndConvertPdf(MultipartFile file) throws IOException {
        FilePdf filePdf = new FilePdf();

        filePdf.setFile_name(file.getOriginalFilename());
        filePdf.setFile_type(file.getContentType());
        filePdf.setFile_data(file.getBytes());

        filePdf = pdfrepo.save(filePdf);
        convertPdfToImages(filePdf);

        return pdfrepo.save(filePdf);
    }

    private void convertPdfToImages(FilePdf filePdf) throws IOException {

        try (PDDocument document = Loader.loadPDF(filePdf.getFile_data())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int page = 0; page < 1; page++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();

                // Lưu hình ảnh vào file hệ thống
                String imageName = filePdf.getFile_name() + "_page_" + (page + 1) + ".jpg";
                Path imagePath = Paths.get(fileUpload, imageName);
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, imageInByte);
//                FileCopyUtils.copy(imageInByte, new File(fileUpload+ "/"+ fileEntity.getFile_name() + "_page_" + (page + 1) + ".jpg"));
//                FilePdf imageEntity = new FilePdf();
//                imageEntity.setImg_name(FilePdf.); + "_page_" + (page + 1) + ".jpg");
//                imageEntity.setData(imageInByte);
//                imageEntity.setFileEntity(fileEntity);

//                imageEntities.add(imageEntity);
                // Lưu dữ liệu hình ảnh vào thực thể FilePdf
                if (page == 0) {  // Lưu chỉ hình ảnh đầu tiên vào thực thể, nếu cần tất cả hình ảnh, cần cập nhật cấu trúc thực thể
                    filePdf.setImg_name(imageName);
                    filePdf.setImg_data(imageInByte);
                }
            }

        }

    }

    public Optional<FilePdf> getFileById(Integer id) {
        return pdfrepo.findById(id);
    }

    public ResponseEntity<ResultDto<?>> createNewBook(BookAdCreateRes bookad) throws IOException {
        try {
            var listcheck = bookrepo.findAll();
            Book newbook=new Book();
            for (Book b : listcheck) {
                if (b.getName() != null && b.getName().equals(bookad.getName())) {
                    ResultDto<?> response = ResultDto.builder().status(false).message("Book is existed").build();
                    return new ResponseEntity<ResultDto<?>>(response, HttpStatus.CONFLICT);
                }
            }
            FilePdf filePdf = uploadAndConvertPdf(bookad.getFile());
            PDDocument document = Loader.loadPDF(filePdf.getFile_data());
            newbook.setEdition(bookad.getEdition());
            newbook.setPrice(bookad.getPrice());
            newbook.setName(bookad.getName());
            newbook.setPublisherDescription(bookad.getPublisherDescription());
            
            newbook.setPageQuantity(document.getNumberOfPages());
            newbook.setRating(0);
            newbook.setRatingQuantity(0);
            // Lưu thông tin của sách
             newbook.setCategories(bookad.getCatelist());
            Book savedBook = bookrepo.save(newbook);
            
            // Liên kết thông tin file PDF với sách vừa lưu
            filePdf.setBook(savedBook);
            pdfrepo.save(filePdf);
           
          
            ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
