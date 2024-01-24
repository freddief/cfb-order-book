package com.cfbenchmarks.interview.operation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.cfbenchmarks.interview.matching.MatchingEngine;
import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.book.PriceLevel;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.match.OrderMatch;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddOrderOperationTest {

  private static final String INSTRUMENT_ID = "instrumentId";
  private static final String ORDER_ID = "orderId";
  private static final String FILLED_ORDER_ID = "filledOrderId";

  @Mock
  private Order incomingOrder;
  @Mock
  private MatchingEngine matchingEngine;
  @Mock
  private AtomicReference<MatchResult> matchResultRef;

  private AddOrderOperation underTest;

  @Mock
  private InstrumentOrderBook instrumentOrderBook;
  @Mock
  private MatchResult matchResult;
  @Mock
  private OrderMatch orderMatch;
  @Mock
  private OrderMatch orderMatch2;
  @Mock
  private Order matchedIncomingOrder;
  @Mock
  private Order matchedBookOrder;
  @Mock
  private Order matchedBookOrder2;

  @BeforeEach
  void setup() {
    underTest = new AddOrderOperation(incomingOrder, matchingEngine, matchResultRef);
  }

  @Test
  void addOrderOperation_whenBookIsEmpty() {
    when(incomingOrder.orderId()).thenReturn(ORDER_ID);
    when(incomingOrder.side()).thenReturn(Side.BUY);
    when(incomingOrder.price()).thenReturn(123L);
    InstrumentOrderBook returned = underTest.apply(INSTRUMENT_ID, null);

    assertThat(returned.getBids()).containsExactly(new PriceLevel(123L, List.of(incomingOrder)));

    verifyNoInteractions(matchingEngine);
    verify(matchResultRef).set(MatchResult.noMatch(incomingOrder));
  }

  @Test
  void addOrderOperation_fullyMatchedOrder() {
    when(matchingEngine.match(instrumentOrderBook, incomingOrder)).thenReturn(matchResult);
    when(matchResult.incomingOrder()).thenReturn(matchedIncomingOrder);
    when(matchedIncomingOrder.isFilled()).thenReturn(true);
    when(matchResult.orderMatches()).thenReturn(List.of(orderMatch, orderMatch2));
    when(orderMatch.bookOrder()).thenReturn(matchedBookOrder);
    when(orderMatch2.bookOrder()).thenReturn(matchedBookOrder2);
    when(matchedBookOrder.isFilled()).thenReturn(false);
    when(matchedBookOrder2.isFilled()).thenReturn(true);
    when(matchedBookOrder2.orderId()).thenReturn(AddOrderOperationTest.FILLED_ORDER_ID);

    InstrumentOrderBook returned = underTest.apply(INSTRUMENT_ID, instrumentOrderBook);

    assertThat(returned).isEqualTo(instrumentOrderBook);
    verify(matchingEngine).match(instrumentOrderBook, incomingOrder);
    verify(matchResultRef).set(matchResult);
    verify(instrumentOrderBook).putOrder(matchedBookOrder);
    verify(instrumentOrderBook).deleteOrder(FILLED_ORDER_ID);
    verifyNoMoreInteractions(incomingOrder, matchingEngine);
  }

  @Test
  void addOrderOperation_partiallyMatchedOrder() {

    when(matchingEngine.match(instrumentOrderBook, incomingOrder)).thenReturn(matchResult);
    when(matchResult.incomingOrder()).thenReturn(matchedIncomingOrder);
    when(matchedIncomingOrder.isFilled()).thenReturn(false);
    when(matchResult.orderMatches()).thenReturn(List.of(orderMatch, orderMatch2));
    when(orderMatch.bookOrder()).thenReturn(matchedBookOrder);
    when(orderMatch2.bookOrder()).thenReturn(matchedBookOrder2);
    when(matchedBookOrder.isFilled()).thenReturn(false);
    when(matchedBookOrder2.isFilled()).thenReturn(true);
    when(matchedBookOrder2.orderId()).thenReturn(AddOrderOperationTest.FILLED_ORDER_ID);

    InstrumentOrderBook returned = underTest.apply(INSTRUMENT_ID, instrumentOrderBook);

    assertThat(returned).isEqualTo(instrumentOrderBook);
    verify(matchingEngine).match(instrumentOrderBook, incomingOrder);
    verify(matchResultRef).set(matchResult);
    verify(instrumentOrderBook).putOrder(matchedIncomingOrder);
    verify(instrumentOrderBook).putOrder(matchedBookOrder);
    verify(instrumentOrderBook).deleteOrder(FILLED_ORDER_ID);
    verifyNoMoreInteractions(incomingOrder, matchingEngine);
  }
}
