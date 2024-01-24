package com.cfbenchmarks.interview.operation;

import static com.cfbenchmarks.interview.model.match.MatchResult.noMatch;

import com.cfbenchmarks.interview.matching.MatchingEngine;
import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.order.Order;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class AddOrderOperation implements AtomicBookOperation {

  private final Order incomingOrder;
  private final MatchingEngine matchingEngine;
  private final AtomicReference<MatchResult> matchResult;

  public AddOrderOperation(
      Order incomingOrder,
      MatchingEngine matchingEngine,
      AtomicReference<MatchResult> matchResult) {
    this.incomingOrder = incomingOrder;
    this.matchingEngine = matchingEngine;
    this.matchResult = matchResult;
  }

  @Override
  public InstrumentOrderBook apply(String instrumentId, InstrumentOrderBook instrumentOrderBook) {

    if (bookIsEmpty(instrumentOrderBook)) {
      matchResult.set(noMatch(incomingOrder));
      return new InstrumentOrderBook(incomingOrder);
    }

    MatchResult matchResult = matchingEngine.match(instrumentOrderBook, incomingOrder);
    this.matchResult.set(matchResult);

    return updateOrderBook(instrumentOrderBook, matchResult);
  }

  private boolean bookIsEmpty(InstrumentOrderBook instrumentOrderBook) {
    return instrumentOrderBook == null;
  }

  private InstrumentOrderBook updateOrderBook(InstrumentOrderBook instrumentOrderBook, MatchResult matchResult) {

    Order incomingOrder = matchResult.incomingOrder();

    if (!incomingOrder.isFilled()) {
      instrumentOrderBook.putOrder(incomingOrder);
    }

    updateMatchedOrders(matchResult, instrumentOrderBook);

    return instrumentOrderBook;
  }

  private void updateMatchedOrders(MatchResult matchResult, InstrumentOrderBook instrumentOrderBook) {

    matchResult
        .orderMatches()
        .forEach(
            orderMatch -> {
              Order updatedBookOrder = orderMatch.bookOrder();

              if (updatedBookOrder.isFilled()) {
                instrumentOrderBook.deleteOrder(updatedBookOrder.orderId());
              } else {
                instrumentOrderBook.putOrder(updatedBookOrder);
              }
            });
  }
}
