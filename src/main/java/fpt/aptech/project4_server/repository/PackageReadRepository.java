package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.PackageRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PackageReadRepository extends JpaRepository<PackageRead, Integer> {
    Optional<PackageRead> findByPackageName(String packageName);
    Optional<PackageRead> findByDayQuantity(int dayQuantity);
}
