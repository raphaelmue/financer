package org.financer.server.domain.model.user;

import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.SettingPair;

import javax.persistence.*;

@Entity
@Table(name = "settings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "property"}))
public class SettingEntity implements DataEntity {
    private static final long serialVersionUID = 3324663296387062431L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne()
    private UserEntity user;

    @Embedded
    private SettingPair pair;

    @Override
    public long getId() {
        return id;
    }

    public SettingEntity setId(long id) {
        this.id = id;
        return this;
    }

    public UserEntity getUser() {
        return user;
    }

    public SettingEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public SettingPair getPair() {
        return pair;
    }

    public SettingEntity setPair(SettingPair pair) {
        this.pair = pair;
        return this;
    }
}
