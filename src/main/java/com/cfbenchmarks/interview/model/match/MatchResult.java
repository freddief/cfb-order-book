package com.cfbenchmarks.interview.model.match;

import com.cfbenchmarks.interview.model.order.Order;

import java.util.List;

public record MatchResult(
        Order incomingOrder,
        List<OrderMatch> orderMatches
) {

    public static MatchResult noMatch(Order incomingOrder) {
        return new MatchResult(incomingOrder, List.of());
    }
}
