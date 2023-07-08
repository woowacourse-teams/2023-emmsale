package com.emmsale.product.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PurchaseTemperature {

  @Column(name = "purchase_temperature", nullable = false)
  private double value;
}
