package org.financer.server.domain.model.user;

import org.financer.server.domain.model.DataEntity;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role implements DataEntity {

    public final static String ROLE_ADMIN = "ADMIN";
    public final static String ROLE_USER = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Override
    public long getId() {
        return id;
    }

    public Role setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Role setName(String name) {
        this.name = name;
        return this;
    }
}
