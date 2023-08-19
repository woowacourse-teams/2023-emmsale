package com.emmsale.config;

import com.support.NPlus1DetectorAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NPlus1DetectorConfig {

  @Bean
  public NPlus1DetectorAop loggingFormAop() {
    return new NPlus1DetectorAop();
  }
}
