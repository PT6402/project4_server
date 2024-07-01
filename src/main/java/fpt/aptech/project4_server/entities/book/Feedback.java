package fpt.aptech.project4_server.entities.book;

import fpt.aptech.project4_server.entities.BaseEntity;
import fpt.aptech.project4_server.entities.user.UserDetail;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tbFeedback")
public class Feedback extends BaseEntity {

    private String content;
    private LocalDateTime feedbackDate;

    @ManyToOne
    @JoinColumn(name = "user_detail_id")
    private UserDetail userDetail;

}
