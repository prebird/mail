package org.prebird.mailworker.config.mail;

import java.util.Map;
import java.util.Properties;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * JavaMailSender 를 빈으로 등록합니다.
 */
@Configuration
public class MailSenderConfig {
  /**
   * 긴급 타입 메일 설정으로 설정된 JavaMailSender
   * @param urgentMailProperties
   * @return
   */
  @Profile({"db-real", "local-test"})
  @Bean
  public JavaMailSender urgentJavaMailSender(UrgentMailProperties urgentMailProperties) {
    return createMailSender(urgentMailProperties);
  }

  /**
   * 일반 메일 설정으로 설정된 JavaMailSender
   * @param normalMailProperties
   * @return
   */
  @Profile({"db-real", "local-test"})
  @Bean
  public JavaMailSender normalJavaMailSender(NormalMailProperties normalMailProperties) {
    return createMailSender(normalMailProperties);
  }

  private JavaMailSenderImpl createMailSender(MailProperties properties) {
    JavaMailSenderImpl sender = new JavaMailSenderImpl();
    this.applyProperties(properties, sender);
    return sender;
  }

  private void applyProperties(MailProperties properties, JavaMailSenderImpl sender) {
    sender.setHost(properties.getHost());
    if (properties.getPort() != null) {
      sender.setPort(properties.getPort());
    }

    sender.setUsername(properties.getUsername());
    sender.setPassword(properties.getPassword());
    sender.setProtocol(properties.getProtocol());
    if (properties.getDefaultEncoding() != null) {
      sender.setDefaultEncoding(properties.getDefaultEncoding().name());
    }

    if (!properties.getProperties().isEmpty()) {
      sender.setJavaMailProperties(this.asProperties(properties.getProperties()));
    }
  }

  private Properties asProperties(Map<String, String> source) {
    Properties properties = new Properties();
    properties.putAll(source);
    return properties;
  }
}
