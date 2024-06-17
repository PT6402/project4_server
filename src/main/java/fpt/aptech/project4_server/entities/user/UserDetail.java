package fpt.aptech.project4_server.entities.user;

import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbUserDetail")
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class UserDetail extends BaseEntity {
    @Column(name = "fullname", columnDefinition = "nvarchar(200)")
    private String fullname;
    private String avartar;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
