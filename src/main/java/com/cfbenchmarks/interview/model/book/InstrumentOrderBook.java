package com.cfbenchmarks.interview.model.book;

import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.*;

/**
 * Mutable data class that provides encapsulated access to the state of an InstrumentOrderBook
 */
public class InstrumentOrderBook {
  /**
   * TreeMap provides ordered structuring of the order book's price levels
   * Orders withing a price level are stored in a LinkedHashMap to maintain insertion order
   */
  protected final TreeMap<Long, LinkedHashMap<String, Order>> bids;
  protected final TreeMap<Long, LinkedHashMap<String, Order>> asks;
  /**
   *
   * This maps an 'orderId' to metadata that identifies its position within the price level TreeMaps.
   * Enables 0(1) access for operations that only have an orderId (such as delete) by preventing the need to iterate over the price level TreeMaps
   *
   */
  protected final Map<String, OrderMetadata> orderMetadata;

  public InstrumentOrderBook(Order order) {
    this.bids = new TreeMap<>(Collections.reverseOrder());
    this.asks = new TreeMap<>();
    this.orderMetadata = new HashMap<>();
    putOrder(order);
  }

  public void putOrder(Order order) {
    TreeMap<Long, LinkedHashMap<String, Order>> priceLevels = getPriceLevels(order.side());
    priceLevels.compute(
            order.price(),
            (price, orders) -> {
              LinkedHashMap<String, Order> orderMap = Objects.requireNonNullElseGet(orders, LinkedHashMap::new);
              orderMetadata.put(order.orderId(), new OrderMetadata(order.side(), order.price()));
              orderMap.put(order.orderId(), order);
              return orderMap;
            });
  }

  public boolean deleteOrder(String orderId) {
    return Optional.ofNullable(orderMetadata.get(orderId))
            .map(mapping -> {
              TreeMap<Long, LinkedHashMap<String, Order>> priceLevels = getPriceLevels(mapping.side());
              LinkedHashMap<String, Order> orders = priceLevels.get(mapping.price());
              orderMetadata.remove(orderId);
              orders.remove(orderId);
              return true;
            })
            .orElse(false);
  }

  public Optional<Long> getBestPrice(Side side) {
    return Optional.of(getPriceLevels(side))
            .map(TreeMap::firstEntry)
            .map(Map.Entry::getValue)
            .map(LinkedHashMap::values)
            .flatMap(v -> v.stream().findFirst().map(Order::price));
  }

  public List<Order> getOrdersAtLevel(Side side, long price) {
    return Optional.of(getPriceLevels(side))
            .map(levels -> levels.get(price))
            .map(levels -> List.copyOf(levels.values()))
            .orElse(List.of());
  }

  public List<PriceLevel> getBids() {
    return toExternalPriceLevels(bids);
  }

  public List<PriceLevel> getAsks() {
    return toExternalPriceLevels(asks);
  }

  private TreeMap<Long, LinkedHashMap<String, Order>> getPriceLevels(Side order) {
    return order == Side.BUY ? bids : asks;
  }

  private List<PriceLevel> toExternalPriceLevels(TreeMap<Long, LinkedHashMap<String, Order>> orders) {
    return orders.entrySet().stream()
            .map(priceLevel -> new PriceLevel(
                    priceLevel.getKey(),
                    new ArrayList<>(priceLevel.getValue().values())))
            .toList();
  }

  protected record OrderMetadata(
          Side side,
          Long price) {
  }
}
