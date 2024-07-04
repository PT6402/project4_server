package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.FilePdf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfRepo extends JpaRepository<FilePdf, Integer> {
    
}
