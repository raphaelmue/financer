package org.financer.server.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.ExpireDate;
import org.financer.shared.domain.model.value.objects.IPAddress;
import org.financer.shared.domain.model.value.objects.OperatingSystem;
import org.financer.shared.domain.model.value.objects.TokenString;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tokens", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "token"}))
public class Token implements DataEntity, UserProperty {
    private static final long serialVersionUID = 8834445127500149942L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(targetEntity = User.class, optional = false)
    @ToString.Exclude
    private User user;

    @Embedded
    private TokenString token;

    @Embedded
    private ExpireDate expireDate;

    @Embedded
    private IPAddress ipAddress;

    @Embedded
    private OperatingSystem operatingSystem;

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.getUser().getId() == userId;
    }
}
