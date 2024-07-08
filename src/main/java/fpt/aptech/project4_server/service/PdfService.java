/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.category.BookAdCreateRes;
import fpt.aptech.project4_server.dto.category.BookUserRes;
import fpt.aptech.project4_server.dto.category.BooklistUserRes;
import fpt.aptech.project4_server.dto.category.CateUserRes;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.repository.*;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.stream.Collectors;
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
    @Autowired
    private ImageBookRepo IBrepo;
    @Value("${upload.path}")
    private String fileUpload;

    public FilePdf uploadAndConvertPdf(MultipartFile file) throws IOException {
        FilePdf filePdf = new FilePdf();
//       List<ImagesBook> imageslist = new ImagesBook();

        filePdf.setFile_name(file.getOriginalFilename());
        filePdf.setFile_type(file.getContentType());
        filePdf.setFile_data(file.getBytes());
        
//        filePdf = pdfrepo.save(filePdf);
//        images = IBrepo.save(images);
        convertPdfToImages(filePdf);

        return pdfrepo.save(filePdf);
    }

    private List<ImagesBook> convertPdfToImages(FilePdf filePdf) throws IOException {

        try (PDDocument document = Loader.loadPDF(filePdf.getFile_data())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<ImagesBook> imagesList = new ArrayList<>();

            for (int page = 0; page < 4; page++) {
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

                ImagesBook images = new ImagesBook();
                images.setImage_name(imageName);
                images.setImage_data(imageInByte);
                images.setCover(page == 0);  // Chỉ đặt cover là true cho hình đầu tiên
                images.setPdf(filePdf);

                imagesList.add(images);
            }
        return imagesList;
        }

    }

    public Optional<FilePdf> getFileById(Integer id) {
        return pdfrepo.findById(id);
    }

    public ResponseEntity<ResultDto<?>> createNewBook(BookAdCreateRes bookad) throws IOException {
        try {
            var listcheck = bookrepo.findAll();
            Book newbook = new Book();
            for (Book b : listcheck) {
                if (b.getName() != null && b.getName().equals(bookad.getName())) {
                    ResultDto<?> response = ResultDto.builder().status(false).message("Book is existed").build();
                    return new ResponseEntity<ResultDto<?>>(response, HttpStatus.CONFLICT);
                }
            }
            FilePdf filePdf = new FilePdf();
            filePdf.setFile_name(bookad.getFile().getOriginalFilename());
            filePdf.setFile_type(bookad.getFile().getContentType());
            filePdf.setFile_data(bookad.getFile().getBytes());
           
         
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
            newbook.setAuthors(bookad.getAuthorlist());
            Book savedBook = bookrepo.save(newbook);

            // Liên kết thông tin file PDF với sách vừa lưu
            filePdf.setBook(savedBook);
            var savepdf=pdfrepo.save(filePdf);
             List<ImagesBook> imagelist=convertPdfToImages(savepdf);
             IBrepo.saveAll(imagelist);

            ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
            return new ResponseEntity<ResultDto<?>>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResultDto<?>> BooklistUserShow() {
        try {

            var listbook = bookrepo.findAll().stream().map(c -> {
                ImagesBook image = getImages(c.getFilePdf());
                byte[] fileImage = image != null ? image.getImage_data() : null;

                return BooklistUserRes.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .price(c.getPrice())
                        .rating(c.getRating())
                        .ratingQuantity(c.getRatingQuantity())
                        .fileimage(fileImage)
                        .catelist(c.getCategories())
                        .build();
            }).collect(Collectors.toList());

            ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(listbook).build();
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message("Fail to show").build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

//        
    public ResponseEntity<ResultDto<?>> BookSingleUserShow(int bookId) {
        try {
            Optional<Book> optionalBook = bookrepo.findById(bookId);

            if (optionalBook.isPresent()) {
                Book book = optionalBook.get();

                // Lấy danh sách hình ảnh từ getImage
                List<byte[]> imageDatas = getImage(book.getFilePdf())
                        .orElseThrow(() -> new RuntimeException("No images found"))
                        .stream()
                        .map(ImagesBook::getImage_data)
                        .collect(Collectors.toList());

                // Tạo đối tượng BookUserRes từ thông tin sách
                BookUserRes bookUserRes = BookUserRes.builder()
                        .id(book.getId())
                        .name(book.getName())
                        .price(book.getPrice())
                        .pageQuantity(book.getPageQuantity())
                        .edition(book.getEdition())
                        .publisherDescription(book.getPublisherDescription())
                        .rating(book.getRating())
                        .ratingQuantity(book.getRatingQuantity())
                        .fileimagelist(imageDatas)
                        .catelist(book.getCategories())
                        .build();

                // Tạo ResponseDto thành công
                ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(bookUserRes).build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                // Nếu không tìm thấy sách với id được cung cấp
                ResultDto<?> response = ResultDto.builder().status(false).message("Book not found").build();
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            // Xử lý lỗi
            ResultDto<?> response = ResultDto.builder().status(false).message("Fail to show").build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ImagesBook getImages(FilePdf file) {
        System.out.println(file.getId());
        var listIB = IBrepo.findAll();

        for (ImagesBook c : listIB) {
            if (c.getPdf().getId() == file.getId()) {
                if (c.isCover()) {
                    return c;
                }
            }
        }
        return null;

    }

    public Optional<List<ImagesBook>> getImage(FilePdf file) {
        System.out.println(file.getId());
        var listIB = IBrepo.findAll();

        List<ImagesBook> imagesList = listIB.stream()
                .filter(c -> c.getPdf().getId() == file.getId())
                .collect(Collectors.toList());

        return imagesList.isEmpty() ? Optional.empty() : Optional.of(imagesList);
    }

    public ResponseEntity<ResultDto<?>> UpdateBook(int id, BookAdCreateRes bookres) {
        try {
            Optional<Book> optionalBook = bookrepo.findById(id);
            if (!optionalBook.isPresent()) {
                throw new EntityNotFoundException("Book not found with id: " + id);}
            
                Book existingBook = optionalBook.get();
//                  FilePdf filePdf = new FilePdf();
//                   filePdf.setFile_name(bookres.getFile().getOriginalFilename());
//            filePdf.setFile_type(bookres.getFile().getContentType());
//            filePdf.setFile_data(bookres.getFile().getBytes());
                  PDDocument document = Loader.loadPDF(bookres.getFile().getBytes());
               
                existingBook.setId(id);
                existingBook.setName(bookres.getName());
                existingBook.setPrice(bookres.getPrice());
                existingBook.setPageQuantity(document.getNumberOfPages());
                existingBook.setEdition(bookres.getEdition());
                existingBook.setPublisherDescription(bookres.getPublisherDescription());
                existingBook.setAuthors(bookres.getAuthorlist());
                existingBook.setCategories(bookres.getCatelist());
                var updateBook=bookrepo.save(existingBook);
                 var idpdf=pdfrepo.findById(existingBook.getFilePdf().getId());
                 if(idpdf.isEmpty()){
                       throw new EntityNotFoundException("filepdf not found with id: " );
                 }else{
                     var filePdfupdate=idpdf.get();
                     filePdfupdate.setFile_name(bookres.getFile().getOriginalFilename());
                     filePdfupdate.setFile_type(bookres.getFile().getContentType());
                     filePdfupdate.setFile_data(bookres.getFile().getBytes());
                     filePdfupdate.setBook(updateBook);
                      List<ImagesBook> oldlist=existingBook.getFilePdf().getImagesbook();
                      IBrepo.deleteAll(oldlist);
                     
                 
                        var savepdf=pdfrepo.save(filePdfupdate);
             List<ImagesBook> imagelist=convertPdfToImages(savepdf);
//          
             IBrepo.saveAll(imagelist);
             
                 
                    
                 }
                         
                         

          
//              
                ResultDto<?> response = ResultDto.builder().status(true).message("Update successfully")
                        .model(existingBook)
                        .build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            
             
            
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder().status(false).message("Update fail: " + e.getMessage()).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }
    
    
}
