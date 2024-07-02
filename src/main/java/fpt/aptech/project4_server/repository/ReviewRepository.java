package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
}
