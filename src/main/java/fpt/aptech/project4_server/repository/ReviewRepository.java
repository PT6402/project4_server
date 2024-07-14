package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Review;
import fpt.aptech.project4_server.entities.user.Wishlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("Select b from Review b where b.book.id=:bookId ")
    List<Review> findByBookId(@Param("bookId") Integer bookId);

    @Query("Select b from Review b where b.userDetail.id=:userID AND b.book.id=:bookID")
    Optional<Review> findByBookIdAndUserDetailId(@Param("userID") Integer userID, @Param("bookID") Integer bookID);
}
