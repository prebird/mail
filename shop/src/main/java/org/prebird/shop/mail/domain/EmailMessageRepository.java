package org.prebird.shop.mail.domain;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {

  @Transactional
  @Modifying
  @Query("""
update EmailMessage em 
set em.emailStatus = :emailStatus
  , em.sentAt = :sentAt
where em.id = :id
""")
  void updateCompleteMessageById(Long id, EmailStatus emailStatus, LocalDateTime sentAt);

  List<EmailMessage> findBySentAtIsNotNull();
}
