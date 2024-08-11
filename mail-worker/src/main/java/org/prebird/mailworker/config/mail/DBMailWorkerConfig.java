package org.prebird.mailworker.config.mail;

import jakarta.annotation.Nullable;
import org.prebird.mailworker.domain.EmailMessageRepository;
import org.prebird.mailworker.domain.ErrorLogRepository;
import org.prebird.mailworker.service.ConsoleMailService;
import org.prebird.mailworker.service.JavaMailService;
import org.prebird.mailworker.mailWorker.DatabaseMailWorker.DBAsyncMailProcessor;
import org.prebird.mailworker.mailWorker.DatabaseMailWorker.NormalDBMailWorker;
import org.prebird.mailworker.mailWorker.DatabaseMailWorker.UrgentDBMailWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * MailWorker 의 설정을 위한 Config
 */
@Configuration
public class DBMailWorkerConfig {

  /**
   * 긴급(Urgent) 계정으로 메일을 발송하는 AsyncMailProcessor 를 생성합니다.
   * {@link UrgentDBMailWorker} 에서 사용됩니다.
   * @param env
   * @param emailMessageRepository
   * @param errorLogRepository
   * @param urgentJavaMailSender 긴급 메일 발송기
   * @return
   */
  @Profile({"db-real, db-console"})
  @Bean
  public DBAsyncMailProcessor asyncUrgentMailProcessor(Environment env, EmailMessageRepository emailMessageRepository,
      ErrorLogRepository errorLogRepository, @Nullable JavaMailSender urgentJavaMailSender) {
    if (env.matchesProfiles("db-console", "local-test-console")) {
      return new DBAsyncMailProcessor(new ConsoleMailService(), emailMessageRepository, errorLogRepository);
    }
    return new DBAsyncMailProcessor(new JavaMailService(urgentJavaMailSender), emailMessageRepository, errorLogRepository);
  }

  /**
   * 일반(Normal) 계정으로 메일을 발송하는 AsyncMailProcessor 를 생성합니다.
   * {@link NormalDBMailWorker} 에서 사용됩니다.
   * @param env
   * @param emailMessageRepository
   * @param errorLogRepository
   * @param normalJavaMailSender 일반 메일 발송기
   * @return
   */
  @Profile({"db-real, db-console"})
  @Bean
  public DBAsyncMailProcessor asyncNormalMailProcessor(Environment env, EmailMessageRepository emailMessageRepository,
      ErrorLogRepository errorLogRepository, @Nullable JavaMailSender normalJavaMailSender) {
    if (env.matchesProfiles("db-console", "local-test-console")) {
      return new DBAsyncMailProcessor(new ConsoleMailService(), emailMessageRepository, errorLogRepository);
    }
    return new DBAsyncMailProcessor(new JavaMailService(normalJavaMailSender), emailMessageRepository, errorLogRepository);
  }
}
