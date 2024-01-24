package com.cfbenchmarks.interview.manager;

import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;

import java.util.List;
import java.util.Optional;

// TODO: Not null params

/**
 * All functions in this class should throw if given null parameters
 */
public interface OrderBookManager {

  /**
   * Add new order
   *
   * <p>Orders for the same instrument, on the same side, with the same price should be kept in the
   * order as they arrive
   *
   * @param order new order to add <br>
   * @return MatchResult the result of any matching that the order triggered
   * @see Order
   */
  MatchResult addOrder(Order order);

  /**
   * Delete an existing order. Returns false if no such order exists
   *
   * @param orderId unique identifier of existing order
   * @return True if the order was successfully deleted, false otherwise
   */
  boolean deleteOrder(String orderId);

  /**
   * Get the best price for the instrument and side.
   *
   * <p>For buy orders - the highest price For sell orders - the lowest price
   *
   * @param instrument identifier of an instrument
   * @param side       either buy or sell
   * @return the best price, or Optional.empty() if there're no orders for the instrument on this
   * side
   */
  Optional<Long> getBestPrice(String instrument, Side side);

  /**
   * Get all orders for the instrument on given side with given price
   *
   * <p>Result should contain orders in the same order as they arrive
   *
   * @param instrument identifier of an instrument
   * @param side       either buy or sell
   * @param price      requested price level
   * @return all orders, or empty list if there are no orders for the instrument on this side with
   * this price
   */
  List<Order> getOrdersAtLevel(String instrument, Side side, long price);
}
