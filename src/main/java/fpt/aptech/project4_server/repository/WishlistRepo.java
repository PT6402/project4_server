/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Repository.java to edit this template
 */
package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.dto.statistic.TopLike;
import fpt.aptech.project4_server.entities.user.Mybook;
import fpt.aptech.project4_server.entities.user.Wishlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author macos
 */
public interface WishlistRepo extends JpaRepository<Wishlist, Integer> {

  @Query("Select b from Wishlist b where b.userDetail.id = :userID AND b.book.id = :bookID")
    Optional<Wishlist> findByUserDetailAndBook(@Param("userID") Integer userID, @Param("bookID") Integer bookID);

    @Query("Select c from Wishlist c where c.userDetail.id = :userID")
    List<Wishlist> findByUserDetailId(@Param("userID") Integer userID);


}
