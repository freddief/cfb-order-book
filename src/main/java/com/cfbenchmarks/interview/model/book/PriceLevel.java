package com.cfbenchmarks.interview.model.book;

import com.cfbenchmarks.interview.model.order.Order;

import java.util.List;

public record PriceLevel(
        long price,
        List<Order> orders
) {

}
