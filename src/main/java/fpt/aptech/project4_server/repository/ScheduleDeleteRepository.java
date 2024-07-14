
package fpt.aptech.project4_server.repository;

import fpt.aptech.project4_server.entities.book.ScheduleBookDeletion;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author macos
 */
public interface ScheduleDeleteRepository extends JpaRepository<ScheduleBookDeletion, Integer> {
     List<ScheduleBookDeletion> findByExpiredDateBefore(LocalDateTime now);
}
