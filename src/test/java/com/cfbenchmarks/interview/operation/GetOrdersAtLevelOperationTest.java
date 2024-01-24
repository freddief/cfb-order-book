package com.cfbenchmarks.interview.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetOrdersAtLevelOperationTest {

  private static final String INSTRUMENT_ID = "instrumentId";
  private static final Side SIDE = Side.BUY;
  private static final long PRICE = 123L;

  @Mock
  private AtomicReference<List<Order>> resultRef;
  @Mock
  private InstrumentOrderBook instrumentOrderBook;

  private GetOrdersAtLevelOperation getOrdersAtLevelOperation;

  @Mock
  private Order order;

  @BeforeEach
  void setup() {
    getOrdersAtLevelOperation = new GetOrdersAtLevelOperation(SIDE, PRICE, resultRef);
  }

  @Test
  void getOrdersAtLevelOperation() {
    when(instrumentOrderBook.getOrdersAtLevel(Side.BUY, PRICE)).thenReturn(List.of(order));
    InstrumentOrderBook returned = getOrdersAtLevelOperation.apply(INSTRUMENT_ID, instrumentOrderBook);
    verify(resultRef).set(List.of(order));
    assertThat(returned).isEqualTo(instrumentOrderBook);
  }

  @Test
  void getOrdersAtLevelOperation_nonExistentBook() {
    InstrumentOrderBook returned = getOrdersAtLevelOperation.apply(INSTRUMENT_ID, null);
    verify(resultRef).set(List.of());
    assertThat(returned).isNull();
  }

}
