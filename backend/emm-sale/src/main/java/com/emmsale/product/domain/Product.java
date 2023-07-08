package com.emmsale.product.domain;

import com.emmsale.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String imageUrl;
  @Column(nullable = false)
  private String productUrl;
  @Column(nullable = false)
  private String brandName;
  @Embedded
  private PriceTrack priceTrack;
}
