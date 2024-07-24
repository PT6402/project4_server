package fpt.aptech.project4_server.service;

import fpt.aptech.project4_server.dto.statistic.BookStatistic;
import fpt.aptech.project4_server.dto.statistic.*;
import fpt.aptech.project4_server.repository.*;
import fpt.aptech.project4_server.util.ResultDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
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
    WishlistRepo WLrepo;
    @PersistenceContext
    private EntityManager entityManager;

    public List<BookStatistic> getBookStatistics() {
        String queryStr = "SELECT new fpt.aptech.project4_server.dto.statistic.BookStatistic(b.id, b.name, " +
                "COUNT(m.id), " +
                "SUM(CASE WHEN m.ExpiredDate IS NULL THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN m.ExpiredDate IS NOT NULL THEN 1 ELSE 0 END), " +
                "SUM(COALESCE(od.Price, 0))) " + // Tính tổng giá của các OrderDetail
                "FROM Mybook m " +
                "JOIN m.book b " +
                "LEFT JOIN OrderDetail od ON b.id = od.book.id " + // Chỉnh sửa để join với Book và OrderDetail
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
                .limit(1)
                .map(bookStatistic -> TopBuy.builder()
                        .bookId(bookStatistic.getBookId())
                        .bookName(bookStatistic.getBookName())
                        .boughtBooks(bookStatistic.getBoughtBooks())
                        .build())
                .collect(Collectors.toList());

        return topBuys;
    }

    public List<TopRent> getTopRent() {
        // Lấy danh sách BookStatistic
        List<BookStatistic> bookStatistics = getBookStatistics();

        // Sắp xếp danh sách theo số lượt mua giảm dần và chọn ra 5 sách đầu tiên
        List<TopRent> topRents = bookStatistics.stream()
                .sorted((b1, b2) -> Long.compare(b2.getRentedBooks(), b1.getRentedBooks()))
                .limit(1)
                .map(bookStatistic -> TopRent.builder()
                        .bookId(bookStatistic.getBookId())
                        .bookName(bookStatistic.getBookName())
                        .rentedBooks(bookStatistic.getRentedBooks())
                        .build())
                .collect(Collectors.toList());

        return topRents;
    }

    public List<TopLike> getTopLike() {
        // Lấy danh sách TopLike từ repository
        List<TopLike> topLikes = WLrepo.findTopBooksByWishlistCount();

        // Giới hạn danh sách chỉ lấy 5 book ID có lượt thêm vào wishlist cao nhất
        return topLikes.stream()
                .limit(1)
                .collect(Collectors.toList());
    }
}
