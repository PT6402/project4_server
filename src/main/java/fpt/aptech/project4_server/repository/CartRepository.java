package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.user.Cart;
import fpt.aptech.project4_server.entities.user.UserDetail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUserDetailId(int userDetailId);

}
