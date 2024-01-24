package com.cfbenchmarks.interview.model.match;

import com.cfbenchmarks.interview.model.order.Order;

public record OrderMatch(
        Order incomingOrder,
        Order bookOrder,
        long fillQuantity,
        long price) {
}
