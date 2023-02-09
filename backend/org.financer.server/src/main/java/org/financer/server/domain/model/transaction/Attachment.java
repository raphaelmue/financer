package org.financer.server.domain.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.financer.server.domain.model.DataEntity;
import org.financer.server.domain.model.user.UserProperty;

import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "attachments")
public class Attachment implements DataEntity, UserProperty {
    private static final long serialVersionUID = 7758316425770345150L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(targetEntity = Transaction.class)
    @JoinColumn(name = "transaction_id")
    @ToString.Exclude
    private Transaction transaction;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "content", nullable = false)
    @Lob
    private byte[] content;

    @Override
    public boolean isPropertyOfUser(long userId) {
        return this.getTransaction().isPropertyOfUser(userId);
    }
}
