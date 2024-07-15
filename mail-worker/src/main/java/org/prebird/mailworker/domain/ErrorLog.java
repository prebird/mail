package org.prebird.mailworker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
@Entity
public class ErrorLog {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  public ErrorLog(String content) {
    this.content = content;
  }

  public static String getStackTraceAsString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
