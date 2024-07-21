package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.CurrentPage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CPRepo extends JpaRepository<CurrentPage, Integer> {
    
}
