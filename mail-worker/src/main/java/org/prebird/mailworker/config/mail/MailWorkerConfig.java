package org.prebird.mailworker.config.mail;

import jakarta.annotation.Nullable;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.domain.ErrorLogRepository;
import org.prebird.mailworker.service.ConsoleMailService;
import org.prebird.mailworker.service.JavaMailService;
import org.prebird.mailworker.woker.AsyncMailProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * MailWorker 의 설정을 위한 Config
 */
@Configuration
public class MailWorkerConfig {

  /**
   * 긴급(Urgent) 계정으로 메일을 발송하는 AsyncMailProcessor 를 생성합니다.
   * {@link org.prebird.mailworker.woker.UrgentMailWorker} 에서 사용됩니다.
   * @param env
   * @param emailMessageRepository
   * @param errorLogRepository
   * @param urgentJavaMailSender 긴급 메일 발송기
   * @return
   */
  @Bean
  public AsyncMailProcessor asyncUrgentMailProcessor(Environment env, EmailMessageRepository emailMessageRepository,
      ErrorLogRepository errorLogRepository, @Nullable JavaMailSender urgentJavaMailSender) {
    if (env.matchesProfiles("console", "local-test-console")) {
      return new AsyncMailProcessor(new ConsoleMailService(), emailMessageRepository, errorLogRepository);
    }
    return new AsyncMailProcessor(new JavaMailService(urgentJavaMailSender), emailMessageRepository, errorLogRepository);
  }

  /**
   * 일반(Normal) 계정으로 메일을 발송하는 AsyncMailProcessor 를 생성합니다.
   * {@link org.prebird.mailworker.woker.NormalMailWorker} 에서 사용됩니다.
   * @param env
   * @param emailMessageRepository
   * @param errorLogRepository
   * @param normalJavaMailSender 일반 메일 발송기
   * @return
   */
  @Bean
  public AsyncMailProcessor asyncNormalMailProcessor(Environment env, EmailMessageRepository emailMessageRepository,
      ErrorLogRepository errorLogRepository, @Nullable JavaMailSender normalJavaMailSender) {
    if (env.matchesProfiles("console", "local-test-console")) {
      return new AsyncMailProcessor(new ConsoleMailService(), emailMessageRepository, errorLogRepository);
    }
    return new AsyncMailProcessor(new JavaMailService(normalJavaMailSender), emailMessageRepository, errorLogRepository);
  }
}
