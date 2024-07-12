package org.prebird.shop.mail.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
  List<EmailMessage> findByEmailStatus(EmailStatus emailStatus);
}
