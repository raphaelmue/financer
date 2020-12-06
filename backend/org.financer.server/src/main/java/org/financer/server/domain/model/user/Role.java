package org.financer.server.domain.model.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "roles")
public class Role implements DataEntity {

    public final static String ROLE_ADMIN = "ADMIN";
    public final static String ROLE_USER = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;
}
