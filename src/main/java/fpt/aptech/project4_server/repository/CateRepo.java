package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CateRepo extends JpaRepository<Category, Integer> {
    
}
