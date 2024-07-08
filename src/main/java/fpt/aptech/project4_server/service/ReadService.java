/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.category.BookAdCreateRes;
import fpt.aptech.project4_server.entities.book.CurrentPage;
import fpt.aptech.project4_server.entities.book.ImageRead;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.repository.CPRepo;
import fpt.aptech.project4_server.repository.ImageReadRepo;
import fpt.aptech.project4_server.repository.Mybookrepo;
import fpt.aptech.project4_server.util.ResultDto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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

/**
 *
 * @author macos
 */
@Service
public class ReadService {
    @Autowired
    private CPRepo cprepo;
    
    @Autowired
    private ImageReadRepo irepo;
    
    @Autowired
    private Mybookrepo MBrepo;
    
    @Value("${upload.path}")
    private String fileUpload;
    
    private List<ImageRead> convertPdfToImages(int mybookid, int startIndex) throws IOException {
        Optional<Mybook> optionalMB = MBrepo.findById(mybookid);
        if (optionalMB.isEmpty()) {
            throw new IllegalArgumentException("Mybook not found with id: " + mybookid);
        }

        Mybook existingMB = optionalMB.get();

        try (PDDocument document = Loader.loadPDF(existingMB.getBook().getFilePdf().getFile_data())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<ImageRead> imagesList = new ArrayList<>();
            
            for (int page = startIndex-5;  page < (startIndex +5) && page < document.getNumberOfPages(); page++) {

BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();

                // Lưu hình ảnh vào file hệ thống
                String imageName = existingMB.getBook().getId() + "_page_" + (page + 1) + ".jpg";
//                Path imagePath = Paths.get(fileUpload, imageName);
//                Files.createDirectories(imagePath.getParent());
//                Files.write(imagePath, imageInByte);

                ImageRead imageRead = new ImageRead();
                imageRead.setImage_name(imageName);
                imageRead.setImage_data(imageInByte);
                imageRead.setCurrent_image(page == startIndex-1);  // Chỉ đặt cover là true cho hình đầu tiên
                imageRead.setCurrentpage(existingMB.getCurrentpage());

                imagesList.add(imageRead);
            }

            return imagesList;
        }
    }
    private List<ImageRead> convertPdfToImagesInitial(int mybookid) throws IOException {
        Optional<Mybook> optionalMB = MBrepo.findById(mybookid);
        if (optionalMB.isEmpty()) {
            throw new IllegalArgumentException("Mybook not found with id: " + mybookid);
        }

        Mybook existingMB = optionalMB.get();

        try (PDDocument document = Loader.loadPDF(existingMB.getBook().getFilePdf().getFile_data())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<ImageRead> imagesList = new ArrayList<>();

            for (int page = 0; page <  10 && page < document.getNumberOfPages(); page++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();

                // Lưu hình ảnh vào file hệ thống
                String imageName = existingMB.getBook().getId() + "_page_" + (page + 1) + ".jpg";
//                Path imagePath = Paths.get(fileUpload, imageName);
//                Files.createDirectories(imagePath.getParent());
//                Files.write(imagePath, imageInByte);

                ImageRead imageRead = new ImageRead();
                imageRead.setImage_name(imageName);
                imageRead.setImage_data(imageInByte);
                imageRead.setCurrent_image(page == 0);  // Chỉ đặt cover là true cho hình đầu tiên
                imageRead.setCurrentpage(existingMB.getCurrentpage());

                imagesList.add(imageRead);
            }

            return imagesList;
        }
    }
    

    public ResponseEntity<ResultDto<?>> createCurrentPageInitial(int index, int mybookid) {
    try {
        Optional<Mybook> optionalMB = MBrepo.findById(mybookid);
        if (optionalMB.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResultDto.builder()
                            .status(false)
                            .message("Mybook not found")
                            .build());
        }

        Mybook existingMB = optionalMB.get();
        CurrentPage currentpage;

        // Kiểm tra xem Mybook có CurrentPage chưa
        if (existingMB.getCurrentpage() != null) {
            // Nếu có, lấy CurrentPage hiện tại và cập nhật
            currentpage = existingMB.getCurrentpage();
            currentpage.setCurrenPageIndex(index);
            currentpage.setImagePageData(existingMB.getBook().getFilePdf().getFile_data());
        } else {
            // Nếu không có, tạo CurrentPage mới
            currentpage = new CurrentPage();
            currentpage.setCurrenPageIndex(index);
            currentpage.setImagePageData(existingMB.getBook().getFilePdf().getFile_data());
            currentpage.setMybook(existingMB);
            CurrentPage savedCurrentPage = cprepo.save(currentpage);
            existingMB.setCurrentpage(savedCurrentPage);
            MBrepo.save(existingMB);
        }

        // Cập nhật lại CurrentPage
        cprepo.save(currentpage);

        // Lấy danh sách ImageRead cũ từ CurrentPage
        List<ImageRead> existingImages = irepo.findByCurrentpage(currentpage.getId());

        // Xóa các ImageRead cũ
        irepo.deleteAll(existingImages);

        // Tạo danh sách ImageRead mới
        List<ImageRead> imagelist;
        if (index == 0) {
            imagelist = convertPdfToImagesInitial(mybookid);
        } else {
            imagelist = convertPdfToImages(mybookid, index);
        }

        // Cập nhật CurrentPage cho các ImageRead mới
        for (ImageRead imageRead : imagelist) {
            imageRead.setCurrentpage(currentpage);
        }

        // Lưu danh sách ImageRead mới
        irepo.saveAll(imagelist);

        ResultDto<?> response = ResultDto.builder().status(true).message("Create successfully").build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
        e.printStackTrace(); // In lỗi ra console để kiểm tra
        ResultDto<?> response = ResultDto.builder().status(false).message(e.getMessage()).build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

}