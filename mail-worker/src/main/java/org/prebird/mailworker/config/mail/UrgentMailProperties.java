package org.prebird.mailworker.config.mail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * urgent 타입 메일 설정
 */
@PropertySource("classpath:application-normal-mail.yml")
@ConfigurationProperties(prefix = "spring.mail.urgent")
@Configuration
public class UrgentMailProperties extends MailProperties {

}
