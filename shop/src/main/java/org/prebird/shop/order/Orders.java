package org.prebird.shop.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prebird.shop.member.Member;


@Builder @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @AllArgsConstructor
@Entity
public class Orders {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "buyer_id")
  private Member buyer;

  private Integer price;
}
