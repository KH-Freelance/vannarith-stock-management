package com.hfsolution.app.config.restclient;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig extends BaseRestClientConfig {
  
  @Bean
  RestClientRequestInterceptor requestInterceptor() {
     return new RestClientRequestInterceptor();
   }
}
