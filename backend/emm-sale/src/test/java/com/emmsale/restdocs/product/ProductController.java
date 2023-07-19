package com.emmsale.restdocs.product;

import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/restdocs/products")
class ProductController {

  @GetMapping("/top-price-drop")
  public List<ProductResponse> topPriceDropProducts() {

    final ProductResponse product1 = new ProductResponse(3896L, "최근 가격 하락 상품1",
        "https://product1-image-url.com", 30, new BigDecimal(7_000),
        new BigDecimal(10_000), 38.5, true);

    final ProductResponse product2 = new ProductResponse(285L, "최근 가격 하락 상품2",
        "https://product2-image-url.com", 70, new BigDecimal(3_000),
        new BigDecimal(10_000), 80.0, false);

    final ProductResponse product3 = new ProductResponse(2345L, "최근 가격 하락 상품3",
        "https://product3-image-url.com", 10, new BigDecimal(72_000),
        new BigDecimal(80_000), 36.5, true);

    final ProductResponse product4 = new ProductResponse(845L, "최근 가격 하락 상품4",
        "https://product4-image-url.com", 0, new BigDecimal(50_000),
        new BigDecimal(50_000), 0.0, false);

    return List.of(product1, product2, product3, product4);
  }

  @GetMapping("/search")
  public List<ProductResponse> searchProducts() {

    final ProductResponse product1 = new ProductResponse(947L, "검색 상품1",
        "https://product1-image-url.com", 30, new BigDecimal(7_000),
        new BigDecimal(10_000), 38.5, true);

    final ProductResponse product2 = new ProductResponse(35L, "검색 상품2",
        "https://product2-image-url.com", 70, new BigDecimal(3_000),
        new BigDecimal(10_000), 80.0, false);

    return List.of(product1, product2);
  }

  @GetMapping("/wish")
  public ResponseEntity<List<ProductResponse>> wishProducts(final HttpServletRequest request,
      @RequestParam final Boolean notificationFilter) {

    if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (notificationFilter) {
      final ProductResponse product1 = new ProductResponse(1584L, "알림 상품1",
          "https://product1-image-url.com", 30, new BigDecimal(7_000),
          new BigDecimal(10_000), 38.5, true);

      final ProductResponse product2 = new ProductResponse(1875L, "알림 상품2",
          "https://product2-image-url.com", 70, new BigDecimal(3_000),
          new BigDecimal(10_000), 80.0, true);

      return ResponseEntity.ok().body(List.of(product1, product2));
    }

    final ProductResponse product1 = new ProductResponse(1584L, "찜 상품1",
        "https://product1-image-url.com", 30, new BigDecimal(7_000),
        new BigDecimal(10_000), 38.5, true);

    final ProductResponse product2 = new ProductResponse(2857L, "찜 상품2",
        "https://product2-image-url.com", 70, new BigDecimal(3_000),
        new BigDecimal(10_000), 80.0, false);

    return ResponseEntity.ok().body(List.of(product1, product2));
  }
}
