package fpt.aptech.project4_server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fpt.aptech.project4_server.entities.auth.User;
import fpt.aptech.project4_server.entities.user.UserDetail;
import java.util.List;

@Repository
public interface UserDetailRepo extends JpaRepository<UserDetail, Integer> {
    Optional<UserDetail> findByUser(User user);

    @Query("select d from UserDetail d where d.user.id=:userId")
    Optional<UserDetail> findByUserId(@Param("userId") int userId);
}
