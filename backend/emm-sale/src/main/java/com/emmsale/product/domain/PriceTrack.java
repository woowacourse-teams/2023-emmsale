package com.emmsale.product.domain;

import com.emmsale.price_history.domain.PriceHistory;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.OneToMany;

@Embeddable
public class PriceTrack {

  @Column(nullable = false)
  private BigDecimal originalPrice;
  @Column(nullable = false)
  private BigDecimal currentPrice;
  @OneToMany(mappedBy = "product")
  private List<PriceHistory> histories;
  @Embedded
  private PurchaseTemperature purchaseTemperature;
}
