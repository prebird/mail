package org.prebird.loadtester.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TpsDto {
  private Long time;
  private Double tps;
}
