package com.cfbenchmarks.interview.model.book;

import static com.cfbenchmarks.interview.model.order.Side.BUY;
import static com.cfbenchmarks.interview.model.order.Side.SELL;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook.OrderMetadata;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class InstrumentOrderBookTest {

  @Test
  void constructor_addsOrderToBook() {
    String orderId = UUID.randomUUID().toString();
    Side side = BUY;
    long price = 123L;

    Order order = order(orderId, side, price);

    InstrumentOrderBook book = new InstrumentOrderBook(order);

    assertThat(book.asks).isEmpty();
    assertThat(book.bids)
            .containsExactly(
                    entry(123L, new LinkedHashMap<>() {{
                      put(orderId, order);
                    }}));
    assertThat(book.orderMetadata).containsExactly(entry(orderId, new OrderMetadata(side, price)));
  }

  @Test
  void putOrder_whenGivenBuyOrders_thenAddsOrdersToBook() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    String orderId3 = UUID.randomUUID().toString();
    Side side = BUY;
    long price = 123L;
    long price2 = 456L;

    Order existingOrder = order(orderId, side, price);
    Order samePriceLevelOrder = order(orderId2, side, price);
    Order differentPriceLevelOrder = order(orderId3, side, price2);

    InstrumentOrderBook book = givenBookHasOrder(existingOrder);

    book.putOrder(samePriceLevelOrder);
    book.putOrder(differentPriceLevelOrder);

    assertThat(book.asks).isEmpty();
    assertThat(book.bids)
            .containsExactly(
                    entry(456L, new LinkedHashMap<>() {{
                      put(orderId3, differentPriceLevelOrder);
                    }}),
                    entry(123L, new LinkedHashMap<>() {{
                      put(orderId, existingOrder);
                      put(orderId2, samePriceLevelOrder);
                    }}));
    assertThat(book.orderMetadata.entrySet())
            .containsExactlyInAnyOrder(
                    entry(orderId, new OrderMetadata(side, price)),
                    entry(orderId2, new OrderMetadata(side, price)),
                    entry(orderId3, new OrderMetadata(side, price2)));
  }

  @Test
  void putOrder_whenGivenSellOrders_thenAddsOrdersToBook() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    String orderId3 = UUID.randomUUID().toString();
    Side side = SELL;
    long price = 123L;
    long price2 = 456L;

    Order existingOrder = order(orderId, side, price);
    Order samePriceLevelOrder = order(orderId2, side, price);
    Order differentPriceLevelOrder = order(orderId3, side, price2);

    InstrumentOrderBook book = givenBookHasOrder(existingOrder);

    book.putOrder(samePriceLevelOrder);
    book.putOrder(differentPriceLevelOrder);

    assertThat(book.bids).isEmpty();
    assertThat(book.asks)
            .containsExactly(
                    entry(123L, new LinkedHashMap<>() {{
                      put(orderId, existingOrder);
                      put(orderId2, samePriceLevelOrder);
                    }}),
                    entry(456L, new LinkedHashMap<>() {{
                      put(orderId3, differentPriceLevelOrder);
                    }}));
    assertThat(book.orderMetadata.entrySet())
            .containsExactlyInAnyOrder(
                    entry(orderId, new OrderMetadata(side, price)),
                    entry(orderId2, new OrderMetadata(side, price)),
                    entry(orderId3, new OrderMetadata(side, price2)));
  }

  @Test
  void deleteOrder_whenGivenBuyOrder_thenDeletesIfExists() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    Side side = BUY;
    long price = 123L;

    Order existingOrder = order(orderId, side, price);
    Order existingOrder2 = order(orderId2, side, price);

    InstrumentOrderBook book = givenBookHasOrders(existingOrder, existingOrder2);

    boolean deleteResponse = book.deleteOrder(orderId);
    boolean nonExistingDeleteResponse = book.deleteOrder("non-existing-id");

    assertThat(deleteResponse).isTrue();
    assertThat(nonExistingDeleteResponse).isFalse();
    assertThat(book.asks).isEmpty();
    assertThat(book.bids)
            .containsExactly(
                    entry(123L, new LinkedHashMap<>() {{
                      put(orderId2, existingOrder2);
                    }}));
    assertThat(book.orderMetadata).containsExactly(entry(orderId2, new OrderMetadata(side, price)));
  }

  @Test
  void deleteOrder_whenGivenSellOrder_thenDeletesIfExists() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    Side side = SELL;
    long price = 123L;

    Order existingOrder = order(orderId, side, price);
    Order existingOrder2 = order(orderId2, side, price);

    InstrumentOrderBook book = givenBookHasOrders(existingOrder, existingOrder2);

    boolean deleteResponse = book.deleteOrder(orderId);
    boolean nonExistingDeleteResponse = book.deleteOrder("non-existing-id");

    assertThat(deleteResponse).isTrue();
    assertThat(nonExistingDeleteResponse).isFalse();
    assertThat(book.bids).isEmpty();
    assertThat(book.asks)
            .containsExactly(
                    entry(123L, new LinkedHashMap<>() {{
                      put(orderId2, existingOrder2);
                    }}));
    assertThat(book.orderMetadata).containsExactly(entry(orderId2, new OrderMetadata(side, price)));
  }

  @Test
  void getBestPrice_givenZeroOrdersForSide_returnsOptionalEmpty() {
    String orderId = UUID.randomUUID().toString();
    Side side = BUY;
    long price = 123L;

    Order order = order(orderId, side, price);

    InstrumentOrderBook book = givenBookHasOrders(order);

    Optional<Long> bestPrice = book.getBestPrice(SELL);

    assertThat(bestPrice).isEmpty();
  }

  @Test
  void getBestPrice_givenMultipleBuyOrders_getsHighestPrice() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    String orderId3 = UUID.randomUUID().toString();
    Side side = BUY;
    long price = 123L;
    long price2 = 127L;
    long price3 = 124L;

    Order order = order(orderId, side, price);
    Order order2 = order(orderId2, side, price2);
    Order order3 = order(orderId3, side, price3);

    InstrumentOrderBook book = givenBookHasOrders(order, order2, order3);

    Optional<Long> bestPrice = book.getBestPrice(side);

    assertThat(bestPrice).contains(127L);
  }

  @Test
  void getBestPrice_givenMultipleSellOrders_getsLowestPrice() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    String orderId3 = UUID.randomUUID().toString();
    Side side = SELL;
    long price = 124L;
    long price2 = 123L;
    long price3 = 127L;

    Order order = order(orderId, side, price);
    Order order2 = order(orderId2, side, price2);
    Order order3 = order(orderId3, side, price3);

    InstrumentOrderBook book = givenBookHasOrders(order, order2, order3);

    Optional<Long> bestPrice = book.getBestPrice(side);

    assertThat(bestPrice).contains(123L);
  }

  @Test
  void getOrdersAtLevel_givenZeroOrdersForLevel_returnsEmptyList() {
    String orderId = UUID.randomUUID().toString();
    Side side = BUY;
    long price = 123L;

    Order order = order(orderId, side, price);

    InstrumentOrderBook book = givenBookHasOrders(order);

    List<Order> orders = book.getOrdersAtLevel(SELL, 456L);

    assertThat(orders).isEmpty();
  }

  @Test
  void getOrdersAtLevel_returnsOrdersAtLevels() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    String orderId3 = UUID.randomUUID().toString();
    Side side = SELL;
    long price = 123L;
    long price2 = 123L;
    long price3 = 127L;

    Order order = order(orderId, side, price);
    Order order2 = order(orderId2, side, price2);
    Order order3 = order(orderId3, side, price3);

    InstrumentOrderBook book = givenBookHasOrders(order, order2, order3);

    List<Order> orders = book.getOrdersAtLevel(SELL, 123L);

    assertThat(orders).containsExactly(order, order2);
  }

  @Test
  void getBids() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    String orderId3 = UUID.randomUUID().toString();
    Side side = BUY;
    long price = 123L;
    long price2 = 123L;
    long price3 = 127L;

    Order order = order(orderId, side, price);
    Order order2 = order(orderId2, side, price2);
    Order order3 = order(orderId3, side, price3);

    InstrumentOrderBook book = givenBookHasOrders(order, order2, order3);

    List<PriceLevel> priceLevels = book.getBids();

    assertThat(priceLevels)
            .containsExactly(
                    new PriceLevel(127L, List.of(order3)),
                    new PriceLevel(123L, List.of(order, order2)));
  }

  @Test
  void getAsks() {
    String orderId = UUID.randomUUID().toString();
    String orderId2 = UUID.randomUUID().toString();
    String orderId3 = UUID.randomUUID().toString();
    Side side = SELL;
    long price = 123L;
    long price2 = 123L;
    long price3 = 127L;

    Order order = order(orderId, side, price);
    Order order2 = order(orderId2, side, price2);
    Order order3 = order(orderId3, side, price3);

    InstrumentOrderBook book = givenBookHasOrders(order, order2, order3);

    List<PriceLevel> priceLevels = book.getAsks();

    assertThat(priceLevels)
            .containsExactly(
                    new PriceLevel(123L, List.of(order, order2)),
                    new PriceLevel(127L, List.of(order3)));
  }

  private InstrumentOrderBook givenBookHasOrders(Order... orders) {
    List<Order> ordersToAdd = List.of(orders);
    Order firstOrder = ordersToAdd.get(0);
    InstrumentOrderBook book = new InstrumentOrderBook(firstOrder);
    ordersToAdd.stream().skip(1).forEach(book::putOrder);
    return book;
  }

  private InstrumentOrderBook givenBookHasOrder(Order order) {
    return new InstrumentOrderBook(order);
  }

  private Order order(String orderId, Side side, long price) {
    Order order = mock(Order.class);
    when(order.orderId()).thenReturn(orderId);
    when(order.side()).thenReturn(side);
    when(order.price()).thenReturn(price);
    return order;
  }
}
