package com.cfbenchmarks.interview.operation;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class GetBestPriceOperation implements AtomicBookOperation {

  private final Side side;
  private final AtomicReference<Optional<Long>> result;

  public GetBestPriceOperation(Side side, AtomicReference<Optional<Long>> result) {
    this.side = side;
    this.result = result;
  }

  @Override
  public InstrumentOrderBook apply(String instrumentId, InstrumentOrderBook instrumentOrderBook) {

    Optional<Long> result = Optional
            .ofNullable(instrumentOrderBook)
            .flatMap(o -> o.getBestPrice(side));

    this.result.set(result);

    return instrumentOrderBook;
  }
}
