/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.author.AuthorShow;
import fpt.aptech.project4_server.dto.author.AuthorUserRes;
import fpt.aptech.project4_server.dto.book.BookAdCreateRes;
import fpt.aptech.project4_server.dto.book.BookFilter;
import fpt.aptech.project4_server.dto.book.BookPagnination;
import fpt.aptech.project4_server.dto.book.BookUserRes;
import fpt.aptech.project4_server.dto.book.BooklistUserRes;
import fpt.aptech.project4_server.dto.book.Paginations;
import fpt.aptech.project4_server.dto.category.CateShow;
import fpt.aptech.project4_server.dto.category.CateUserRes;
import fpt.aptech.project4_server.dto.packageread.PackageAdCreateRes;
import fpt.aptech.project4_server.dto.packageread.PackageShowbook;
import fpt.aptech.project4_server.dto.review.ReviewShow1;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.Category;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.book.PackageRead;
import fpt.aptech.project4_server.entities.book.ScheduleBookDeletion;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.repository.*;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
    @Autowired
    private PackageReadRepository Prepo;
    @Autowired
    private Mybookrepo MBrepo;
    @Autowired
    private ScheduleDeleteRepository SDrepo;
    @Value("${upload.path}")
    private String fileUpload;

    public FilePdf uploadAndConvertPdf(MultipartFile file) throws IOException {
        FilePdf filePdf = new FilePdf();
        // List<ImagesBook> imageslist = new ImagesBook();

        filePdf.setFile_name(file.getOriginalFilename());
        filePdf.setFile_type(file.getContentType());
        filePdf.setFile_data(file.getBytes());

        // filePdf = pdfrepo.save(filePdf);
        // images = IBrepo.save(images);
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
                images.setCover(page == 0); // Chỉ đặt cover là true cho hình đầu tiên
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
            newbook.setStatusMybook(false);
            Book savedBook = bookrepo.save(newbook);

            // Liên kết thông tin file PDF với sách vừa lưu
            filePdf.setBook(savedBook);
            var savepdf = pdfrepo.save(filePdf);
            List<ImagesBook> imagelist = convertPdfToImages(savepdf);
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
                List<CateShow> catshowlist = c.getCategories().stream()
                        .map(category -> new CateShow(category.getId(), category.getName()))
                        .toList();
                List<AuthorShow> authorshowlist = c.getAuthors().stream()
                        .map(author -> new AuthorShow(author.getId(), author.getName()))
                        .toList();
                return BooklistUserRes.builder()
                        .id(c.getId())
                        .name(c.getName())
                        // .price(c.getPrice())
                        .rating(c.getRating())
                        .ratingQuantity(c.getRatingQuantity())
                        .fileimage(fileImage)
                        .catelist(catshowlist)
                        .authorlist(authorshowlist)
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
                // Chuyển đổi danh sách Category thành danh sách CategoryRes
                List<CateShow> categoryResList = book.getCategories().stream()
                        .map(category -> new CateShow(category.getId(), category.getName()))
                        .collect(Collectors.toList());

                // Chuyển đổi danh sách Author thành danh sách AuthorRes
                List<AuthorUserRes> authorResList = book.getAuthors().stream()
                        .map(author -> new AuthorUserRes(author.getId(), author.getName(), author.getImage_data()))
                        .collect(Collectors.toList());
                List<ReviewShow1> reviewList = book.getReview().stream()
                        .map(review -> new ReviewShow1(review.getContent(), review.getRating(), review.getId(),
                                review.getUserDetail().getId(), review.getUserDetail().getFullname()))
                        .collect(Collectors.toList());

                List<PackageRead> packageReadList = Prepo.findAll();
                int maxDayQuantity = packageReadList.stream()
                        .mapToInt(PackageRead::getDayQuantity)
                        .max()
                        .orElse(1);
                List<PackageShowbook> packageList = Prepo.findAll().stream()
                        .map(packageRead -> {
                            BigDecimal price = BigDecimal.valueOf(book.getPrice());

                            double rentPrice = price.divide(BigDecimal.valueOf(maxDayQuantity), 5, RoundingMode.HALF_UP)

                                    .multiply(BigDecimal.valueOf(packageRead.getDayQuantity()))
                                    .setScale(0, RoundingMode.HALF_UP)
                                    .doubleValue();
                            return new PackageShowbook(
                                    packageRead.getPackageName(),
                                    packageRead.getDayQuantity(),
                                    rentPrice

                        );
                        })
                        .collect(Collectors.toList());

                BookUserRes bookUserRes = BookUserRes.builder()
                        .id(book.getId())
                        .name(book.getName())
                        .pageQuantity(book.getPageQuantity())
                        .packlist(packageList)
                        .edition(book.getEdition())
                        .publisherDescription(book.getPublisherDescription())
                        .rating(book.getRating())
                        .ratingQuantity(book.getRatingQuantity())
                        .fileimagelist(imageDatas)
                        .catelist(categoryResList)
                        .authorlist(authorResList)
                        .reviewlist(reviewList)
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
                throw new EntityNotFoundException("Book not found with id: " + id);
            }

            Book existingBook = optionalBook.get();
            // FilePdf filePdf = new FilePdf();
            // filePdf.setFile_name(bookres.getFile().getOriginalFilename());
            // filePdf.setFile_type(bookres.getFile().getContentType());
            // filePdf.setFile_data(bookres.getFile().getBytes());
            PDDocument document = Loader.loadPDF(bookres.getFile().getBytes());

            existingBook.setId(id);
            existingBook.setName(bookres.getName());
            existingBook.setPrice(bookres.getPrice());
            existingBook.setPageQuantity(document.getNumberOfPages());
            existingBook.setEdition(bookres.getEdition());
            existingBook.setPublisherDescription(bookres.getPublisherDescription());
            existingBook.setAuthors(bookres.getAuthorlist());
            existingBook.setCategories(bookres.getCatelist());
            var updateBook = bookrepo.save(existingBook);
            var idpdf = pdfrepo.findById(existingBook.getFilePdf().getId());
            if (idpdf.isEmpty()) {
                throw new EntityNotFoundException("filepdf not found with id: ");
            } else {
                var filePdfupdate = idpdf.get();
                filePdfupdate.setFile_name(bookres.getFile().getOriginalFilename());
                filePdfupdate.setFile_type(bookres.getFile().getContentType());
                filePdfupdate.setFile_data(bookres.getFile().getBytes());
                filePdfupdate.setBook(updateBook);
                List<ImagesBook> oldlist = existingBook.getFilePdf().getImagesbook();
                IBrepo.deleteAll(oldlist);

                var savepdf = pdfrepo.save(filePdfupdate);
                List<ImagesBook> imagelist = convertPdfToImages(savepdf);
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

    public ResponseEntity<ResultDto<?>> Pagnination(int page, int limit) {
        try {
            // Lấy tất cả các sách từ bookrepo
            List<Book> allBooks = bookrepo.findAll();
            int totalBooks = allBooks.size();
            System.out.println(totalBooks);
            // Tính toán chỉ số bắt đầu và kết thúc cho trang hiện tại
            if (page == 1) {
                int start = Math.min(page - 1, totalBooks);
                int end = Math.min(page * limit, totalBooks);
                List<Book> paginatedBooks = allBooks.subList(start, end);

                List<BookPagnination> bookPagninations = paginatedBooks.stream().map(c -> {
                    ImagesBook image = getImages(c.getFilePdf());
                    byte[] fileImage = image != null ? image.getImage_data() : null;

                    return BookPagnination.builder()
                            .bookid(c.getId())
                            .name(c.getName())
                            .rating(c.getRating())
                            .ratingQuantity(c.getRatingQuantity())
                            .ImageCove(fileImage)
                            .build();
                }).collect(Collectors.toList());
                Paginations pag = new Paginations();
                pag.setPaglist(bookPagninations);
                if (totalBooks < limit) {
                    pag.setTotalPage(1);
                } else if (limit % totalBooks == 0) {
                    pag.setTotalPage(limit / totalBooks);
                } else {
                    pag.setTotalPage(limit / totalBooks + 1);
                }

                ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(pag).build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                int start = Math.min((page - 1) * limit, totalBooks);
                int end = Math.min(page * limit, totalBooks);
                List<Book> paginatedBooks = allBooks.subList(start, end);

                List<BookPagnination> bookPagninations = paginatedBooks.stream().map(c -> {
                    ImagesBook image = getImages(c.getFilePdf());
                    byte[] fileImage = image != null ? image.getImage_data() : null;

                    return BookPagnination.builder()
                            .bookid(c.getId())
                            .name(c.getName())
                            .rating(c.getRating())
                            .ratingQuantity(c.getRatingQuantity())
                            .ImageCove(fileImage)
                            .build();
                }).collect(Collectors.toList());
                Paginations pag = new Paginations();
                pag.setPaglist(bookPagninations);
                if (totalBooks < limit) {
                    pag.setTotalPage(1);
                } else if (limit % totalBooks == 0) {
                    pag.setTotalPage(limit / totalBooks);
                } else {
                    pag.setTotalPage(limit / totalBooks + 1);
                }
                ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(pag).build();
                return new ResponseEntity<>(response, HttpStatus.OK);

            }

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message("Fail to show")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResultDto<?>> Filter(int page, int limit, BookFilter bf) {
        try {
            List<Book> books;

            // Kiểm tra nếu không có filter nào được cung cấp
            if ((bf.getList() == null || bf.getList().isEmpty()) && (bf.getRating() == null)) {
                books = bookrepo.findAll();
            } else {
                // Lấy tất cả các sách có cate_id trong danh sách từ BookFilter nếu danh sách
                // không rỗng
                if (bf.getList() != null && !bf.getList().isEmpty()) {
                    books = new ArrayList<>();
                    for (Integer cateId : bf.getList()) {
                        List<Book> booksByCateId = bookrepo.findByCateId(cateId);
                        books.addAll(booksByCateId);
                    }
                } else {
                    books = bookrepo.findAll();
                }

                // Lọc những cuốn sách có rating từ 0 đến rating của BookFilter nếu rating không
                // null
                if (bf.getRating() != null) {
                    books = books.stream()
                            .filter(book -> book.getRating() >= 0 && book.getRating() <= bf.getRating())
                            .collect(Collectors.toList());
                }
            }

            int totalBooks = books.size();
            List<Book> paginatedBooks;
            if (page == 1) {
                int start = Math.min(page - 1, totalBooks);
                int end = Math.min(page * limit, totalBooks);
                paginatedBooks = books.subList(start, end);
                List<BookPagnination> bookPagninations = paginatedBooks.stream().map(c -> {
                    ImagesBook image = getImages(c.getFilePdf());
                    byte[] fileImage = image != null ? image.getImage_data() : null;

                    return BookPagnination.builder()
                            .bookid(c.getId())
                            .name(c.getName())
                            .rating(c.getRating())
                            .ratingQuantity(c.getRatingQuantity())
                            .ImageCove(fileImage)
                            .build();
                }).collect(Collectors.toList());

                Paginations pag = new Paginations();
                pag.setPaglist(bookPagninations);
                if (totalBooks < limit) {
                    pag.setTotalPage(1);
                } else if (limit % totalBooks == 0) {
                    pag.setTotalPage(limit / totalBooks);
                } else {
                    pag.setTotalPage(limit / totalBooks + 1);
                }

                ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(pag).build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                int start = Math.min((page - 1) * limit, totalBooks);
                int end = Math.min(page * limit, totalBooks);
                paginatedBooks = books.subList(start, end);
                List<BookPagnination> bookPagninations = paginatedBooks.stream().map(c -> {
                    ImagesBook image = getImages(c.getFilePdf());
                    byte[] fileImage = image != null ? image.getImage_data() : null;

                    return BookPagnination.builder()
                            .bookid(c.getId())
                            .name(c.getName())
                            .rating(c.getRating())
                            .ratingQuantity(c.getRatingQuantity())
                            .ImageCove(fileImage)
                            .build();
                }).collect(Collectors.toList());

                Paginations pag = new Paginations();
                pag.setPaglist(bookPagninations);
                if (totalBooks < limit) {
                    pag.setTotalPage(1);
                } else if (limit % totalBooks == 0) {
                    pag.setTotalPage(limit / totalBooks);
                } else {
                    pag.setTotalPage(limit / totalBooks + 1);
                }

                ResultDto<?> response = ResultDto.builder().status(true).message("ok").model(pag).build();
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message("Fail to show")
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResultDto<?> deleteBookById(int bookId) {
        try {
            // Kiểm tra xem sách có tồn tại không
            Optional<Book> optionalBook = bookrepo.findById(bookId);
            if (!optionalBook.isPresent()) {
                ResultDto<?> response = ResultDto.builder()
                        .status(false)
                        .message("Book not found with id: " + bookId)
                        .build();
                return response;
            }

            Book book = optionalBook.get();

            // Kiểm tra trạng thái của sách
            ResultDto<?> checkStatusResult = checkStatus(bookId);
            if (!checkStatusResult.isStatus()) {
                return checkStatusResult;
            }

            Object model = checkStatusResult.getModel();
            if (model == null || (model instanceof Boolean && !(Boolean) model)) {
                // Xóa ngay sách nếu statusMybook là false
                // Xử lý xóa các liên kết và sách
                handleBookDeletion(book);

                ResultDto<?> response = ResultDto.builder()
                        .status(true)
                        .message("Delete successfully")
                        .build();
                return response;

            } else {
                // Đánh dấu cần xóa và chờ đến ngày hết hạn
                ScheduleBookDeletion scheduledBookDeletion = new ScheduleBookDeletion();
                scheduledBookDeletion.setBookId(bookId);

                // Lấy ngày hết hạn trễ nhất từ kết quả checkStatusResult
                if (model instanceof LocalDateTime) {
                    scheduledBookDeletion.setExpiredDate((LocalDateTime) model);
                } else {
                    // Nếu không có ngày hết hạn thì có thể xử lý mặc định ở đây
                    // Ví dụ:
                    // scheduledBookDeletion.setExpiredDate(LocalDateTime.now().plusDays(30));
                    // Hoặc trả về lỗi nếu không có ngày hết hạn
                    ResultDto<?> response = ResultDto.builder()
                            .status(false)
                            .message("Failed to schedule deletion. Missing expiry date.")
                            .build();
                    return response;
                }

                SDrepo.save(scheduledBookDeletion);

                ResultDto<?> response = ResultDto.builder()
                        .status(false)
                        .message("Book has active mybook. It will be deleted after the expired date.")
                        .model(bookId) // Lưu bookId vào model để lên lịch xoá sau này
                        .build();
                return response;
            }
        } catch (Exception e) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message("Delete fail: " + e.getMessage())
                    .build();
            return response;
        }
    }

    // Hàm kiểm tra và cập nhật trạng thái của sách
    public ResultDto<?> checkStatus(int bookId) {
        Optional<Book> optionalBook = bookrepo.findById(bookId);
        if (!optionalBook.isPresent()) {
            ResultDto<?> response = ResultDto.builder()
                    .status(false)
                    .message("Book not found with id: " + bookId)
                    .build();
            return response;
        }

        Book book = optionalBook.get();
        LocalDateTime now = LocalDateTime.now();

        // Tìm mybook có expiredDate lớn hơn hiện tại và lớn nhất
        Optional<LocalDateTime> latestExpiredDate = MBrepo.findByBookId(bookId).stream()
                .map(Mybook::getExpiredDate)
                .filter(expiredDate -> expiredDate.isAfter(now))
                .max(LocalDateTime::compareTo);

        // Kiểm tra và cập nhật trạng thái statusMybook của book
        if (latestExpiredDate.isPresent()) {
            book.setStatusMybook(true);
            bookrepo.save(book);
            ResultDto<?> response = ResultDto.builder()
                    .status(true)
                    .message("Book has active mybook")
                    .model(latestExpiredDate.get()) // Trả về latestExpiredDate trong model
                    .build();
            return response;
        } else {
            book.setStatusMybook(false);
            bookrepo.save(book);
            ResultDto<?> response = ResultDto.builder()
                    .status(true)
                    .message("Book has no active mybook")
                    .build();
            return response;
        }
    }

    // Hàm xử lý xóa các liên kết và sách
    private void handleBookDeletion(Book book) {
        // Xóa liên kết với các bảng khác nếu cần
        if (book.getFilePdf() != null) {
            book.getFilePdf().setBook(null);
        }

        book.getAuthors().clear();
        book.getCategories().clear();
        book.getPages().clear();
        book.getMybook().forEach(mybook -> {
            if (mybook.getCurrentpage() != null) {
                mybook.setCurrentpage(null);
            }
        });
        book.getMybook().clear();

        bookrepo.delete(book);
    }

}
