package org.prebird.shop.load;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @Builder
@NoArgsConstructor
@Entity
public class LoadTest {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String description;
  private Long successCount;
  private Long failCount;
  private LocalDateTime firstRequestAt;
  private LocalDateTime lastSentAt;
  private Double duration;
  private Double tps;
  private Double averageTimePerMail;
  private Double maxTimePerMail;

  public void setDescription(String description){
    this.description = description;
  }
}
