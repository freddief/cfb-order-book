package com.cfbenchmarks.interview.matching;

import static com.cfbenchmarks.interview.model.order.Side.BUY;

import com.cfbenchmarks.interview.model.book.InstrumentOrderBook;
import com.cfbenchmarks.interview.model.book.PriceLevel;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.match.OrderMatch;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MatchingEngineImpl implements MatchingEngine {

  @Override
  public MatchResult match(InstrumentOrderBook orderBook, Order incomingOrder) {

    Collection<PriceLevel> otherSidePriceLevels = getOtherSidePriceLevels(orderBook, incomingOrder);
    AtomicReference<Order> incomingOrderRef = new AtomicReference<>(incomingOrder);

    List<OrderMatch> orderMatches =
            otherSidePriceLevels.stream()
                    .filter(priceLevel -> orderCrossesPriceLevel(priceLevel, incomingOrder))
                    .takeWhile(e -> incomingOrderIsNotFilled(incomingOrderRef))
                    .map(priceLevel -> matchPriceLevel(priceLevel, incomingOrderRef))
                    .flatMap(Collection::stream)
                    .toList();

    return new MatchResult(incomingOrderRef.get(), orderMatches);
  }

  private List<OrderMatch> matchPriceLevel(PriceLevel priceLevel, AtomicReference<Order> incomingOrder) {
    return priceLevel.orders().stream()
            .takeWhile(e -> incomingOrderIsNotFilled(incomingOrder))
            .map(bookOrder -> {

              Order order = incomingOrder.get();
              long currentRemainingQuantity = order.remainingQuantity();
              long bookOrderRemainingQuantity = bookOrder.remainingQuantity();
              long fillQuantity = fillQuantity(currentRemainingQuantity, bookOrderRemainingQuantity);
              long price = bookOrder.price();
              Order matchedIncomingOrder = order.increaseFilledQuantity(fillQuantity);
              Order matchedBookOrder = bookOrder.increaseFilledQuantity(fillQuantity);

              incomingOrder.set(matchedIncomingOrder);

              return new OrderMatch(matchedIncomingOrder, matchedBookOrder, fillQuantity, price);
            })
            .toList();
  }

  private boolean incomingOrderIsNotFilled(AtomicReference<Order> updatedIncomingOrder) {
    return !updatedIncomingOrder.get().isFilled();
  }

  private Collection<PriceLevel> getOtherSidePriceLevels(InstrumentOrderBook orderBook, Order order) {
    return order.side() == BUY ? orderBook.getAsks() : orderBook.getBids();
  }

  private boolean orderCrossesPriceLevel(PriceLevel priceLevel, Order incomingOrder) {

    long priceLevelPrice = priceLevel.price();
    long orderPrice = incomingOrder.price();
    Side side = incomingOrder.side();

    return side == BUY
            ? orderPrice >= priceLevelPrice
            : orderPrice <= priceLevelPrice;
  }

  private long fillQuantity(long remainingQuantity, long bookOrderRemainingQuantity) {
    return Math.min(remainingQuantity, bookOrderRemainingQuantity);
  }
}
