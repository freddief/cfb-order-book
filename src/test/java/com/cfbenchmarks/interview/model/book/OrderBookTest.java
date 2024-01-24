package com.cfbenchmarks.interview.model.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.cfbenchmarks.interview.operation.AtomicBookOperation;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderBookTest {

  private static final String INSTRUMENT_ID = "instrumentId";

  @Mock
  private ConcurrentHashMap<String, InstrumentOrderBook> instrumentOrderBooks;
  @Mock
  private AtomicBookOperation atomicBookOperation;
  @InjectMocks
  private OrderBook orderBook;

  @Test
  void atomicBookOperation() {

    orderBook.atomicBookOperation(INSTRUMENT_ID, atomicBookOperation);

    verify(instrumentOrderBooks).compute(eq(INSTRUMENT_ID), eq(atomicBookOperation));
  }

  @Test
  void instruments() {

    OrderBook book =
            new OrderBook(new ConcurrentHashMap<>() {{
              put(INSTRUMENT_ID, mock(InstrumentOrderBook.class));
            }});

    List<String> returned = book.instruments();

    assertThat(returned).containsExactly(INSTRUMENT_ID);
  }
}
