package com.cfbenchmarks.interview.operation;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeleteOrderOperation implements AtomicBookOperation {

  private final String orderId;
  private final AtomicBoolean deleteResult;

  public DeleteOrderOperation(String orderId, AtomicBoolean deleteResult) {
    this.orderId = orderId;
    this.deleteResult = deleteResult;
  }

  @Override
  public InstrumentOrderBook apply(String instrumentId, InstrumentOrderBook instrumentOrderBook) {
    boolean result = instrumentOrderBook.deleteOrder(orderId);
    this.deleteResult.set(result);
    return instrumentOrderBook;
  }
}
