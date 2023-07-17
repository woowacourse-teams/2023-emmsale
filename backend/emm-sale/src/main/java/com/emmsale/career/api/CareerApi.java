package com.emmsale.career.api;

import com.emmsale.career.application.CareerService;
import com.emmsale.career.application.dto.CareerResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/careers")
@RequiredArgsConstructor
public class CareerApi {

  private final CareerService careerService;

  @GetMapping
  public ResponseEntity<List<CareerResponse>> findAll(){
    return ResponseEntity.ok(careerService.findAll());
  }
}

