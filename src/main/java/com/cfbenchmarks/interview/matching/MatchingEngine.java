package com.cfbenchmarks.interview.matching;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.order.Order;

public interface MatchingEngine {
  MatchResult match(InstrumentOrderBook orderBook, Order incomingOrder);
}
