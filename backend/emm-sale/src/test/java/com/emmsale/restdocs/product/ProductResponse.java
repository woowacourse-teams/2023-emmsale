package com.emmsale.restdocs.product;

import java.math.BigDecimal;

class ProductResponse {

  private final long id;
  private final String name;
  private final String imageUrl;
  private final int discountRate;
  private final BigDecimal currentPrice;
  private final BigDecimal originalPrice;
  private final double purchaseTemperature;
  private final boolean isNotified;

  public ProductResponse(final long id, final String name, final String imageUrl,
      final int discountRate,
      final BigDecimal currentPrice, final BigDecimal originalPrice,
      final double purchaseTemperature,
      final boolean isNotified) {
    this.id = id;
    this.name = name;
    this.imageUrl = imageUrl;
    this.discountRate = discountRate;
    this.currentPrice = currentPrice;
    this.originalPrice = originalPrice;
    this.purchaseTemperature = purchaseTemperature;
    this.isNotified = isNotified;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public int getDiscountRate() {
    return discountRate;
  }

  public BigDecimal getCurrentPrice() {
    return currentPrice;
  }

  public BigDecimal getOriginalPrice() {
    return originalPrice;
  }

  public double getPurchaseTemperature() {
    return purchaseTemperature;
  }

  public boolean isNotified() {
    return isNotified;
  }
}
