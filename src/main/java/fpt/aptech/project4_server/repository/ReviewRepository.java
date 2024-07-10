package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query
    ("Select b from Review b where b.book.id=:bookId ")
    List<Review> findByBookId(@Param("bookId") Integer bookId);
}
