package org.financer.server.domain.repository;

import org.financer.server.domain.model.transaction.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Long> {
}
