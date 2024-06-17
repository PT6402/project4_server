package fpt.aptech.project4_server.entities.user;

import fpt.aptech.project4_server.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "tbToken")
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Token extends BaseEntity {

    private String token;
    private boolean revoked;
    private boolean expired;
    private boolean isResetPassword;

    @Enumerated(EnumType.STRING)
    private TypeDevice typeDevice;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
