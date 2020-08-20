package org.financer.server.domain.model.user;

import org.financer.server.domain.model.DataEntity;
import org.financer.shared.domain.model.value.objects.SettingPair;

import javax.persistence.*;

@Entity
@Table(name = "settings", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "property"}))
public class Setting implements DataEntity {
    private static final long serialVersionUID = 3324663296387062431L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne()
    private User user;

    @Embedded
    private SettingPair pair;

    public Setting setValue(String value) {
        this.setPair(this.getPair().setValue(value));
        return this;
    }

    /*
     * Getters and Setters
     */

    @Override
    public long getId() {
        return id;
    }

    public Setting setId(long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Setting setUser(User user) {
        this.user = user;
        return this;
    }

    public SettingPair getPair() {
        return pair;
    }

    public Setting setPair(SettingPair pair) {
        this.pair = pair;
        return this;
    }
}
