package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    
}
