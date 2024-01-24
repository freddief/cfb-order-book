package com.cfbenchmarks.interview.model.book;

import com.cfbenchmarks.interview.operation.AtomicBookOperation;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/** Mutable data class that provides synchronised access to the `InstrumentOrderBook`s */
public class OrderBook {

  /** ConcurrentHashMap enables atomic operations on an InstrumentOrderBook */
  protected final ConcurrentHashMap<String, InstrumentOrderBook> instrumentOrderBooks;

  public OrderBook(ConcurrentHashMap<String, InstrumentOrderBook> instrumentOrderBooks) {
    this.instrumentOrderBooks = instrumentOrderBooks;
  }

  /**
   * Uses the compute() method of ConcurrentHashMap to enable atomic operations on an InstrumentOrderBook
   *
   * @param instrumentId the instrument ID
   * @param operation the operation
   */
  public void atomicBookOperation(String instrumentId, AtomicBookOperation operation) {
    instrumentOrderBooks.compute(instrumentId, operation);
  }

  public List<String> instruments() {
    return instrumentOrderBooks.keySet().stream().toList();
  }
}
