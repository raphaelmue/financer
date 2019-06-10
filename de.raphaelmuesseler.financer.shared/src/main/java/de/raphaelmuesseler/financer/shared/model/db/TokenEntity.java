package de.raphaelmuesseler.financer.shared.model.db;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users_tokens")
public class TokenEntity implements DataEntity, Cloneable {
    private static final long serialVersionUID = 8834445127500149942L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne()
    private UserEntity user;

    @Column(name = "token")
    private String token;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "system")
    private String system;

    @Column(name = "is_mobile")
    private boolean isMobile;

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public boolean getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(boolean mobile) {
        isMobile = mobile;
    }

    @Override
    public TokenEntity clone() {
        try {
            return (TokenEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ClassCastException(e.getMessage());
        }
    }
}
