package com.cfbenchmarks.interview.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetBestPriceOperationTest {

  private static final String INSTRUMENT_ID = "instrumentId";
  private static final Side side = Side.BUY;
  private static final long PRICE = 123L;

  @Mock
  private AtomicReference<Optional<Long>> resultRef;
  @Mock
  private InstrumentOrderBook instrumentOrderBook;

  private GetBestPriceOperation getBestPriceOperation;

  @Mock
  private Order order;

  @BeforeEach
  void setup() {
    getBestPriceOperation = new GetBestPriceOperation(side, resultRef);
  }

  @Test
  void getBestPriceOperation() {
    when(instrumentOrderBook.getBestPrice(Side.BUY)).thenReturn(Optional.of(PRICE));
    InstrumentOrderBook returned = getBestPriceOperation.apply(INSTRUMENT_ID, instrumentOrderBook);
    verify(resultRef).set(Optional.of(PRICE));
    assertThat(returned).isEqualTo(instrumentOrderBook);
  }

  @Test
  void getBestPriceOperation_nonExistentBook() {
    InstrumentOrderBook returned = getBestPriceOperation.apply(INSTRUMENT_ID, null);
    verify(resultRef).set(Optional.empty());
    assertThat(returned).isNull();
  }

}
