package com.cfbenchmarks.interview.config;

import com.cfbenchmarks.interview.model.book.OrderBook;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderBookConfig {

  @Bean
  public OrderBook orderBook() {
    return new OrderBook(new ConcurrentHashMap<>());
  }
}
