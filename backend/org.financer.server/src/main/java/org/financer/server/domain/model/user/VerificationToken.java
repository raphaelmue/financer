package org.financer.server.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.ExpireDate;
import org.financer.shared.domain.model.value.objects.TokenString;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "verification_tokens", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "token"}))
public class VerificationToken implements DataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(targetEntity = User.class, optional = false)
    private User user;

    @Embedded
    private TokenString token;

    @Embedded
    private ExpireDate expireDate;

    @Column(name = "verifying_date")
    private LocalDate verifyingDate;
}
