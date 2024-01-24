package com.cfbenchmarks.interview.operation;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class GetOrdersAtLevelOperation implements AtomicBookOperation {

  private final Side side;
  private final Long price;
  private final AtomicReference<List<Order>> result;

  public GetOrdersAtLevelOperation(Side side, Long price, AtomicReference<List<Order>> result) {
    this.side = side;
    this.price = price;
    this.result = result;
  }

  @Override
  public InstrumentOrderBook apply(String instrumentId, InstrumentOrderBook instrumentOrderBook) {
    List<Order> result = Optional.ofNullable(instrumentOrderBook)
            .map(i -> instrumentOrderBook.getOrdersAtLevel(side, price))
            .orElse(List.of());
    this.result.set(result);
    return instrumentOrderBook;
  }
}
