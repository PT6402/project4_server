package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.statistic.BookStatistic;
import fpt.aptech.project4_server.dto.statistic.*;
import fpt.aptech.project4_server.entities.book.Book;
import fpt.aptech.project4_server.entities.book.FilePdf;
import fpt.aptech.project4_server.entities.book.ImagesBook;
import fpt.aptech.project4_server.entities.user.Wishlist;
import fpt.aptech.project4_server.repository.*;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    @Autowired
    BookRepo Brepo;
    @Autowired
    Mybookrepo MBrepo;
    @Autowired
    OrderDetailRepository Orepo;
    @Autowired
    private ImageBookRepo IBrepo;

    @Autowired
    WishlistRepo WLrepo;
    @PersistenceContext
    private EntityManager entityManager;

    public List<BookStatistic> getBookStatistics() {
        String queryStr = "SELECT new fpt.aptech.project4_server.dto.statistic.BookStatistic(b.id, b.name, "
                + "COUNT(m.id), "
                + "SUM(CASE WHEN m.ExpiredDate IS NULL THEN 1 ELSE 0 END), "
                + "SUM(CASE WHEN m.ExpiredDate IS NOT NULL THEN 1 ELSE 0 END), "
                + "SUM(COALESCE(od.Price, 0))) "
                + // Tính tổng giá của các OrderDetail
                "FROM Mybook m "
                + "JOIN m.book b "
                + "LEFT JOIN OrderDetail od ON b.id = od.book.id "
                + // Chỉnh sửa để join với Book và OrderDetail
                "GROUP BY b.id, b.name";
        TypedQuery<BookStatistic> query = entityManager.createQuery(queryStr, BookStatistic.class);
        return query.getResultList();
    }

    public List<TopBuy> getTopBuy() {
        // Lấy danh sách BookStatistic
        List<BookStatistic> bookStatistics = getBookStatistics();

        // Sắp xếp danh sách theo số lượt mua giảm dần và chọn ra 5 sách đầu tiên
        List<TopBuy> topBuys = bookStatistics.stream()
                .sorted((b1, b2) -> Long.compare(b2.getBoughtBooks(), b1.getBoughtBooks()))
                .limit(5)
                .map(bookStatistic -> {
                    // Lấy FilePdf từ bookId
                    Book book = Brepo.findById(bookStatistic.getBookId()).orElse(null);
                    if (book != null) {
                        FilePdf filePdf = book.getFilePdf();

                        // Lấy hình ảnh từ FilePdf
                        ImagesBook image = getImage(filePdf);
                        byte[] fileImage = image != null ? image.getImage_data() : null;

                        return TopBuy.builder()
                                .bookId(bookStatistic.getBookId())
                                .bookName(bookStatistic.getBookName())
                                .boughtBooks(bookStatistic.getBoughtBooks())
                                .Imagedata(fileImage)
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return topBuys;
    }

    public List<TopRent> getTopRent() {
        // Lấy danh sách BookStatistic
        List<BookStatistic> bookStatistics = getBookStatistics();

        // Sắp xếp danh sách theo số lượt thuê giảm dần và chọn ra 5 sách đầu tiên
        List<TopRent> topRents = bookStatistics.stream()
                .sorted((b1, b2) -> Long.compare(b2.getRentedBooks(), b1.getRentedBooks()))
                .limit(5)
                .map(bookStatistic -> {
                    // Lấy FilePdf từ bookId
                    Book book = Brepo.findById(bookStatistic.getBookId()).orElse(null);
                    if (book != null) {
                        FilePdf filePdf = book.getFilePdf();

                        // Lấy hình ảnh từ FilePdf
                        ImagesBook image = getImage(filePdf);
                        byte[] fileImage = image != null ? image.getImage_data() : null;

                        return TopRent.builder()
                                .bookId(bookStatistic.getBookId())
                                .bookName(bookStatistic.getBookName())
                                .rentedBooks(bookStatistic.getRentedBooks())
                                .Imagedata(fileImage)
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return topRents;
    }

    public List<TopLike> findTopBooksByWishlistCount() {
        List<Wishlist> wishlists = WLrepo.findAll();
        Map<Integer, TopLike> bookCountMap = new HashMap<>();

        for (Wishlist wishlist : wishlists) {
            Book book = wishlist.getBook();
            if (book != null) {
                TopLike topLike = bookCountMap.getOrDefault(book.getId(), new TopLike());
                topLike.setBookId(book.getId());
                topLike.setBookName(book.getName());
                topLike.setRating(book.getRating());
                topLike.setPrice(book.getPrice());

                // Assuming you want to get the cover image data
                if (book.getFilePdf() != null && book.getFilePdf().getImagesbook() != null) {
                    for (ImagesBook image : book.getFilePdf().getImagesbook()) {
                        if (image.isCover()) {
                            topLike.setImagedata(image.getImage_data());
                            break;
                        }
                    }
                }

                topLike.setLikeQty(topLike.getLikeQty() != null ? topLike.getLikeQty() + 1 : 1);
                bookCountMap.put(book.getId(), topLike);
            }
        }

        List<TopLike> topLikes = new ArrayList<>(bookCountMap.values());
        topLikes.sort((a, b) -> Long.compare(b.getLikeQty(), a.getLikeQty()));

        // Return top 5 books
        return topLikes.stream().limit(5).collect(Collectors.toList());
    }

    public List<NewRelease> getTop4BooksByCreateAt() {
    Pageable pageable = PageRequest.of(0, 4);
    List<Book> topBooks = Brepo.findAll(pageable).getContent();
    
    List<NewRelease> newReleases = topBooks.stream()
            .map(book -> {
                FilePdf filePdf = book.getFilePdf();

                // Lấy hình ảnh từ FilePdf
                ImagesBook image = getImage(filePdf);
                byte[] fileImage = image != null ? image.getImage_data() : null;

                return NewRelease.builder()
                        .bookId(book.getId())
                        .bookName(book.getName())
                        .rating(book.getRating())
                        .Imagedata(fileImage)
                        .build();
            })
            .collect(Collectors.toList());

    return newReleases;
}


    public ImagesBook getImage(FilePdf file) {
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
}
