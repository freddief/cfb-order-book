package com.cfbenchmarks.interview.factory;

import com.cfbenchmarks.interview.matching.MatchingEngine;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;
import com.cfbenchmarks.interview.operation.AddOrderOperation;
import com.cfbenchmarks.interview.operation.DeleteOrderOperation;
import com.cfbenchmarks.interview.operation.GetBestPriceOperation;
import com.cfbenchmarks.interview.operation.GetOrdersAtLevelOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BookOperationFactory {

  private final MatchingEngine matchingEngine;

  @Autowired
  public BookOperationFactory(MatchingEngine matchingEngine) {
    this.matchingEngine = matchingEngine;
  }

  public AddOrderOperation addOrderOperation(Order order, AtomicReference<MatchResult> result) {
    return new AddOrderOperation(order, matchingEngine, result);
  }

  public DeleteOrderOperation deleteOrderOperation(String orderId, AtomicBoolean result) {
    return new DeleteOrderOperation(orderId, result);
  }

  public GetBestPriceOperation getBestPriceOperation(Side side, AtomicReference<Optional<Long>> result) {
    return new GetBestPriceOperation(side, result);
  }

  public GetOrdersAtLevelOperation getOrdersAtLevelOperation(Side side, long price, AtomicReference<List<Order>> result) {
    return new GetOrdersAtLevelOperation(side, price, result);
  }
}
