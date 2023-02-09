package org.financer.server.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.SettingPair;

import jakarta.persistence.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "settings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "property"}))
public class Setting implements DataEntity {
    private static final long serialVersionUID = 3324663296387062431L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne()
    @ToString.Exclude
    private User user;

    @Embedded
    private SettingPair pair;

    public Setting setValue(String value) {
        this.setPair(this.getPair().setValue(value));
        return this;
    }
}
