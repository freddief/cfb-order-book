package com.cfbenchmarks.interview.manager;

import static com.cfbenchmarks.interview.model.order.Side.BUY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.cfbenchmarks.interview.factory.BookOperationFactory;
import com.cfbenchmarks.interview.manager.OrderBookManagerImpl;
import com.cfbenchmarks.interview.model.book.OrderBook;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.operation.AddOrderOperation;
import com.cfbenchmarks.interview.operation.DeleteOrderOperation;
import com.cfbenchmarks.interview.operation.GetBestPriceOperation;
import com.cfbenchmarks.interview.operation.GetOrdersAtLevelOperation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderBookManagerImplTest {

  private static final String INSTRUMENT_ID = "instrumentId";
  private static final String INSTRUMENT_ID2 = "instrumentId2";
  private static final String ORDER_ID = "orderID";
  private static final long PRICE = 123L;

  @Mock
  private OrderBook orderBook;
  @Mock
  private BookOperationFactory bookOperationFactory;
  @InjectMocks
  private OrderBookManagerImpl underTest;

  @Mock
  private Order order;
  @Mock
  private AddOrderOperation addOrderOperation;
  @Mock
  private DeleteOrderOperation deleteOrderOperation;
  @Mock
  private DeleteOrderOperation deleteOrderOperation2;
  @Mock
  private GetBestPriceOperation getBestPriceOperation;
  @Mock
  private GetOrdersAtLevelOperation getOrdersAtLevelOperation;
  @Mock
  private MatchResult matchResult;

  @Test
  void addOrder() {
    when(order.instrumentId()).thenReturn(INSTRUMENT_ID);
    doAnswer(
            invocation -> {
              AtomicReference<MatchResult> matchResultRef = invocation.getArgument(1);
              matchResultRef.set(matchResult);
              return addOrderOperation;
            })
            .when(bookOperationFactory)
            .addOrderOperation(any(Order.class), any(AtomicReference.class));

    MatchResult returned = underTest.addOrder(order);

    assertThat(returned).isEqualTo(matchResult);
    verify(bookOperationFactory).addOrderOperation(eq(order), any());
    verify(orderBook).atomicBookOperation(INSTRUMENT_ID, addOrderOperation);
  }

  @Test
  void deleteOrder() {

    when(orderBook.instruments()).thenReturn(List.of(INSTRUMENT_ID, INSTRUMENT_ID2));

    doAnswer(
            invocation -> {
              AtomicBoolean returnRef = invocation.getArgument(1);
              returnRef.set(false);
              return deleteOrderOperation;
            })
            .doAnswer(
                    invocation -> {
                      AtomicBoolean returnRef = invocation.getArgument(1);
                      returnRef.set(true);
                      return deleteOrderOperation2;
                    })
            .when(bookOperationFactory)
            .deleteOrderOperation(eq(ORDER_ID), any(AtomicBoolean.class));

    boolean returned = underTest.deleteOrder(ORDER_ID);

    assertThat(returned).isTrue();
    verify(bookOperationFactory, times(2)).deleteOrderOperation(eq(ORDER_ID), any());
    verify(orderBook).atomicBookOperation(INSTRUMENT_ID, deleteOrderOperation);
    verify(orderBook).atomicBookOperation(INSTRUMENT_ID2, deleteOrderOperation2);
  }

  @Test
  void getBestPrice() {
    doAnswer(
            invocation -> {
              AtomicReference<Optional<Long>> bestPriceRef = invocation.getArgument(1);
              bestPriceRef.set(Optional.of(PRICE));
              return getBestPriceOperation;
            })
            .when(bookOperationFactory)
            .getBestPriceOperation(eq(BUY), any(AtomicReference.class));

    Optional<Long> returned = underTest.getBestPrice(INSTRUMENT_ID, BUY);

    assertThat(returned).isEqualTo(Optional.of(PRICE));
    verify(bookOperationFactory).getBestPriceOperation(eq(BUY), any());
    verify(orderBook).atomicBookOperation(INSTRUMENT_ID, getBestPriceOperation);
  }

  @Test
  void getOrdersAtLevel() {
    doAnswer(
            invocation -> {
              AtomicReference<List<Order>> bestPriceRef = invocation.getArgument(2);
              bestPriceRef.set(List.of(order));
              return getOrdersAtLevelOperation;
            })
            .when(bookOperationFactory)
            .getOrdersAtLevelOperation(eq(BUY), eq(PRICE), any(AtomicReference.class));

    List<Order> returned = underTest.getOrdersAtLevel(INSTRUMENT_ID, BUY, PRICE);

    assertThat(returned).isEqualTo(List.of(order));
    verify(bookOperationFactory).getOrdersAtLevelOperation(eq(BUY), eq(PRICE), any());
    verify(orderBook).atomicBookOperation(INSTRUMENT_ID, getOrdersAtLevelOperation);
  }
}
