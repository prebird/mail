package org.prebird.mailworker.domain;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
  List<EmailMessage> findByEmailStatus(EmailStatus emailStatus, Pageable pageable);

  List<EmailMessage> findByEmailStatusAndEmailType(EmailStatus emailStatus, EmailType emailType, Pageable pageable);
}
