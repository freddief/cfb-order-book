package com.cfbenchmarks.interview.matching;

import static com.cfbenchmarks.interview.model.order.Side.BUY;
import static com.cfbenchmarks.interview.model.order.Side.SELL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.match.OrderMatch;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class MatchingEngineImplTest {

  private final MatchingEngine underTest = new MatchingEngineImpl();

  @Test
  void match_givenEmptyBook_thenNoMatches() {
    Order incomingOrder = order(BUY, 10L, 120L);
    InstrumentOrderBook orderBook = mock(InstrumentOrderBook.class);
    when(orderBook.getAsks()).thenReturn(List.of());

    MatchResult matchResult = underTest.match(orderBook, incomingOrder);

    assertThat(matchResult.incomingOrder()).isEqualTo(incomingOrder);
    assertThat(matchResult.orderMatches()).isEmpty();
  }

  @Test
  void match_whenOrderDoesNotCrossTheBook_thenNoMatches() {
    Order incomingOrder = order(BUY, 9L, 120L);

    Order sellOrder = order(SELL, 10L, 240L);
    Order sellOrder2 = order(SELL, 10L, 240L);
    Order sellOrder3 = order(SELL, 11L, 240L);
    Order sellOrder4 = order(SELL, 12L, 240L);

    InstrumentOrderBook orderBook = givenBookHasOrders(sellOrder, sellOrder2, sellOrder3, sellOrder4);

    MatchResult matchResult = underTest.match(orderBook, incomingOrder);

    assertThat(matchResult.incomingOrder()).isEqualTo(incomingOrder);
    assertThat(matchResult.orderMatches()).isEmpty();
  }

  @Test
  void match_whenBuyOrderCanBeFullyFilledByASingleSellOrder_thenMatchesSuccessfully() {

    Order incomingOrder = order(BUY, 14L, 120L);

    Order sellOrder = order(SELL, 10L, 240L);
    Order sellOrder2 = order(SELL, 10L, 240L);
    Order sellOrder3 = order(SELL, 11L, 240L);
    Order sellOrder4 = order(SELL, 12L, 240L);

    InstrumentOrderBook orderBook = givenBookHasOrders(sellOrder, sellOrder2, sellOrder3, sellOrder4);

    MatchResult matchResult = underTest.match(orderBook, incomingOrder);

    Order filledIncomingOrder = incomingOrder.withFilledQuantity(120L);
    Order partFilledBookOrder = sellOrder.withFilledQuantity(120L);

    assertThat(matchResult.incomingOrder()).isEqualTo(filledIncomingOrder);
    assertThat(matchResult.orderMatches())
            .containsExactly(new OrderMatch(filledIncomingOrder, partFilledBookOrder, 120L, 10L));
  }

  @Test
  void match_whenSellOrderCanBeFullyFilledByASingleBuyOrder_thenMatchesSuccessfully() {

    Order incomingOrder = order(SELL, 14L, 120L);

    Order buyOrder = order(BUY, 17L, 240L);
    Order buyOrder2 = order(BUY, 17L, 240L);
    Order buyOrder3 = order(BUY, 15L, 240L);
    Order buyOrder4 = order(BUY, 12L, 240L);

    InstrumentOrderBook orderBook = givenBookHasOrders(buyOrder, buyOrder2, buyOrder3, buyOrder4);

    MatchResult matchResult = underTest.match(orderBook, incomingOrder);

    Order filledIncomingOrder = incomingOrder.withFilledQuantity(120L);
    Order partFilledBookOrder = buyOrder.withFilledQuantity(120L);

    assertThat(matchResult.incomingOrder()).isEqualTo(filledIncomingOrder);
    assertThat(matchResult.orderMatches())
            .containsExactly(new OrderMatch(filledIncomingOrder, partFilledBookOrder, 120L, 17L));
  }

  @Test
  void match_whenOrderCanBeFullyFilledByMultipleOrdersInSamePriceLevel_thenMatchesSuccessfully() {

    Order incomingOrder = order(BUY, 14L, 120L);

    Order sellOrder = order(SELL, 10L, 100L, 20L);
    Order sellOrder2 = order(SELL, 10L, 10L);
    Order sellOrder3 = order(SELL, 10L, 240L);
    Order sellOrder4 = order(SELL, 12L, 240L);

    InstrumentOrderBook orderBook = givenBookHasOrders(sellOrder, sellOrder2, sellOrder3, sellOrder4);

    MatchResult matchResult = underTest.match(orderBook, incomingOrder);

    Order filledIncomingOrder = incomingOrder.withFilledQuantity(120L);

    Order partFilledBookOrder = sellOrder.withFilledQuantity(100L);
    Order partFilledBookOrder2 = sellOrder2.withFilledQuantity(10L);
    Order partFilledBookOrder3 = sellOrder3.withFilledQuantity(30L);

    Order partFilledIncomingOrder = incomingOrder.withFilledQuantity(80L);
    Order partFilledIncomingOrder2 = incomingOrder.withFilledQuantity(90L);

    assertThat(matchResult.incomingOrder()).isEqualTo(filledIncomingOrder);
    assertThat(matchResult.orderMatches())
            .containsExactly(
                    new OrderMatch(partFilledIncomingOrder, partFilledBookOrder, 80L, 10L),
                    new OrderMatch(partFilledIncomingOrder2, partFilledBookOrder2, 10L, 10L),
                    new OrderMatch(filledIncomingOrder, partFilledBookOrder3, 30L, 10L));
  }

  @Test
  void match_whenOrderCanBeFullyFilledByMultipleOrdersInDifferentPriceLevels_thenMatchesSuccessfully() {
    Order incomingOrder = order(BUY, 14L, 120L);

    Order sellOrder = order(SELL, 10L, 100L, 20L);
    Order sellOrder2 = order(SELL, 11L, 10L);
    Order sellOrder3 = order(SELL, 12L, 240L);
    Order sellOrder4 = order(SELL, 12L, 240L);

    InstrumentOrderBook orderBook = givenBookHasOrders(sellOrder, sellOrder2, sellOrder3, sellOrder4);

    MatchResult matchResult = underTest.match(orderBook, incomingOrder);

    Order filledIncomingOrder = incomingOrder.withFilledQuantity(120L);

    Order partFilledBookOrder = sellOrder.withFilledQuantity(100L);
    Order partFilledBookOrder2 = sellOrder2.withFilledQuantity(10L);
    Order partFilledBookOrder3 = sellOrder3.withFilledQuantity(30L);

    Order partFilledIncomingOrder = incomingOrder.withFilledQuantity(80L);
    Order partFilledIncomingOrder2 = incomingOrder.withFilledQuantity(90L);

    assertThat(matchResult.incomingOrder()).isEqualTo(filledIncomingOrder);
    assertThat(matchResult.orderMatches())
            .containsExactly(
                    new OrderMatch(partFilledIncomingOrder, partFilledBookOrder, 80L, 10L),
                    new OrderMatch(partFilledIncomingOrder2, partFilledBookOrder2, 10L, 11L),
                    new OrderMatch(filledIncomingOrder, partFilledBookOrder3, 30L, 12L));
  }

  @Test
  void match_whenOrderCanBePartiallyFilledByMultipleOrdersInSamePriceLevel_thenMatchesSuccessfully() {
    Order incomingOrder = order(BUY, 10L, 120L);

    Order sellOrder = order(SELL, 10L, 100L, 20L);
    Order sellOrder2 = order(SELL, 10L, 10L);
    Order sellOrder3 = order(SELL, 14L, 240L);
    Order sellOrder4 = order(SELL, 12L, 240L);

    InstrumentOrderBook orderBook = givenBookHasOrders(sellOrder, sellOrder2, sellOrder3, sellOrder4);

    MatchResult matchResult = underTest.match(orderBook, incomingOrder);

    Order partFilledBookOrder = sellOrder.withFilledQuantity(100L);
    Order partFilledBookOrder2 = sellOrder2.withFilledQuantity(10L);

    Order partFilledIncomingOrder = incomingOrder.withFilledQuantity(80L);
    Order partFilledIncomingOrder2 = incomingOrder.withFilledQuantity(90L);

    assertThat(matchResult.incomingOrder()).isEqualTo(partFilledIncomingOrder2);
    assertThat(matchResult.orderMatches())
            .containsExactly(
                    new OrderMatch(partFilledIncomingOrder, partFilledBookOrder, 80L, 10L),
                    new OrderMatch(partFilledIncomingOrder2, partFilledBookOrder2, 10L, 10L));
  }

  @Test
  void match_whenOrderCanBePartiallyFilledByMultipleOrdersInDifferentPriceLevels_thenMatchesSuccessfully() {
    Order incomingOrder = order(BUY, 13L, 120L);

    Order sellOrder = order(SELL, 10L, 10L);
    Order sellOrder2 = order(SELL, 10L, 20L);
    Order sellOrder3 = order(SELL, 11L, 20L);
    Order sellOrder4 = order(SELL, 13L, 20L);
    Order sellOrder5 = order(SELL, 14L, 40L);

    InstrumentOrderBook orderBook = givenBookHasOrders(sellOrder, sellOrder2, sellOrder3, sellOrder4, sellOrder5);

    MatchResult matchResult = underTest.match(orderBook, incomingOrder);

    Order partFilledBookOrder = sellOrder.withFilledQuantity(10L);
    Order partFilledBookOrder2 = sellOrder2.withFilledQuantity(20L);
    Order partFilledBookOrder3 = sellOrder3.withFilledQuantity(20L);
    Order partFilledBookOrder4 = sellOrder4.withFilledQuantity(20L);

    Order partFilledIncomingOrder = incomingOrder.withFilledQuantity(10L);
    Order partFilledIncomingOrder2 = incomingOrder.withFilledQuantity(30L);
    Order partFilledIncomingOrder3 = incomingOrder.withFilledQuantity(50L);
    Order partFilledIncomingOrder4 = incomingOrder.withFilledQuantity(70L);

    assertThat(matchResult.incomingOrder()).isEqualTo(partFilledIncomingOrder4);
    assertThat(matchResult.orderMatches())
            .containsExactly(
                    new OrderMatch(partFilledIncomingOrder, partFilledBookOrder, 10L, 10L),
                    new OrderMatch(partFilledIncomingOrder2, partFilledBookOrder2, 20L, 10L),
                    new OrderMatch(partFilledIncomingOrder3, partFilledBookOrder3, 20L, 11L),
                    new OrderMatch(partFilledIncomingOrder4, partFilledBookOrder4, 20L, 13L));
  }

  private InstrumentOrderBook givenBookHasOrders(Order... orders) {
    List<Order> ordersToAdd = List.of(orders);
    Order firstOrder = ordersToAdd.get(0);
    InstrumentOrderBook book = new InstrumentOrderBook(firstOrder);
    ordersToAdd.stream().skip(1).forEach(book::putOrder);
    return book;
  }

  private Order order(Side side, long price, long quantity) {
    return order(side, price, quantity, 0L);
  }

  private Order order(Side side, long price, long quantity, long filledQuantity) {
    return new Order(
            UUID.randomUUID().toString(), "instrumentId", side, price, quantity, filledQuantity);
  }
}
