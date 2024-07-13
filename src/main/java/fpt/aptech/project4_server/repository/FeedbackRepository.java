package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    
}
