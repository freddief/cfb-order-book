package com.cfbenchmarks.interview.operation;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import java.util.function.BiFunction;

public interface AtomicBookOperation extends BiFunction<String, InstrumentOrderBook, InstrumentOrderBook> {}
