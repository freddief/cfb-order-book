package com.cfbenchmarks.interview.manager;

import com.cfbenchmarks.interview.factory.BookOperationFactory;
import com.cfbenchmarks.interview.model.book.OrderBook;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class OrderBookManagerImpl implements OrderBookManager {

  private final OrderBook orderBook;
  private final BookOperationFactory bookOperationFactory;

  @Autowired
  public OrderBookManagerImpl(OrderBook orderBook, BookOperationFactory bookOperationFactory) {
    this.orderBook = orderBook;
    this.bookOperationFactory = bookOperationFactory;
  }

  @Override
  public MatchResult addOrder(Order order) {
    AtomicReference<MatchResult> result = new AtomicReference<>();
    orderBook.atomicBookOperation(
            order.instrumentId(),
            bookOperationFactory.addOrderOperation(order, result));
    return result.get();
  }

  @Override
  public boolean deleteOrder(String orderId) {
    return orderBook.instruments().stream()
            .anyMatch(instrumentId -> {
              AtomicBoolean result = new AtomicBoolean();
              orderBook.atomicBookOperation(
                      instrumentId,
                      bookOperationFactory.deleteOrderOperation(orderId, result));
              return result.get();
            });
  }

  @Override
  public Optional<Long> getBestPrice(String instrument, Side side) {
    AtomicReference<Optional<Long>> result = new AtomicReference<>();
    orderBook.atomicBookOperation(
            instrument,
            bookOperationFactory.getBestPriceOperation(side, result));
    return result.get();
  }

  @Override
  public List<Order> getOrdersAtLevel(String instrument, Side side, long price) {
    AtomicReference<List<Order>> result = new AtomicReference<>();
    orderBook.atomicBookOperation(
            instrument,
            bookOperationFactory.getOrdersAtLevelOperation(side, price, result));
    return result.get();
  }
}
