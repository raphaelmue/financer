package de.raphaelmuesseler.financer.shared.model.db;

import javax.persistence.*;

@Entity
@Table(name = "users_settings")
public class SettingsEntity implements DataEntity {
    private final static long serialVersionUID = 3324663296387062431L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne()
    private UserEntity user;

    @Column(name = "property")
    private String property;

    @Column(name = "value")
    private String value;

    @Override
    public int getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
