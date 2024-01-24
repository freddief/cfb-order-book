package com.cfbenchmarks.interview.cucumber.transformer;

import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;
import io.cucumber.java.DataTableType;

import java.util.Map;
import java.util.Optional;

public class OrderTransformer {

  @DataTableType
  public Order order(Map<String, String> row) {
    return new Order(
            row.get("orderId"),
            row.get("instrumentId"),
            Side.valueOf(row.get("side")),
            Long.parseLong(row.get("price")),
            Long.parseLong(row.get("quantity")),
            Optional.ofNullable(row.get("filledQuantity"))
                    .map(Long::parseLong)
                    .orElse(0L));
  }
}
