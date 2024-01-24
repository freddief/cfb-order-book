package com.cfbenchmarks.interview.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteOrderOperationTest {

  private static final String INSTRUMENT_ID = "instrumentId";
  private static final String ORDER_ID = "orderId";

  @Mock
  private AtomicBoolean resultRef;
  @Mock
  private InstrumentOrderBook instrumentOrderBook;

  private DeleteOrderOperation deleteOrderOperation;

  @BeforeEach
  void setup() {
    deleteOrderOperation = new DeleteOrderOperation(ORDER_ID, resultRef);
  }

  @Test
  void deleteOrderOperation() {
    when(instrumentOrderBook.deleteOrder(ORDER_ID)).thenReturn(true);
    InstrumentOrderBook returned = deleteOrderOperation.apply(INSTRUMENT_ID, instrumentOrderBook);
    verify(resultRef).set(true);
    assertThat(returned).isEqualTo(instrumentOrderBook);
  }
}
