package com.cfbenchmarks.interview.model.book;

import com.cfbenchmarks.interview.model.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderBookTestHelper {

  @Autowired
  private OrderBook orderBook;

  public void clearBooks(){
    orderBook.instrumentOrderBooks.clear();
  }

  public void clearBook(String instrument) {
    orderBook.instrumentOrderBooks.remove(instrument);
  }

  public List<Order> getBuys(String instrument) {
    return orderBook.instrumentOrderBooks.get(instrument)
            .getBids()
            .stream()
            .map(PriceLevel::orders)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
  }

  public List<Order> getSells(String instrument) {
    return orderBook.instrumentOrderBooks.get(instrument)
            .getAsks()
            .stream()
            .map(PriceLevel::orders)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
  }

}
