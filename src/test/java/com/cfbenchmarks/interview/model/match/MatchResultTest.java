package com.cfbenchmarks.interview.model.match;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.cfbenchmarks.interview.model.order.Order;
import org.junit.jupiter.api.Test;

class MatchResultTest {

  @Test
  void noMatch() {
    Order order = mock(Order.class);
    MatchResult matchResult = MatchResult.noMatch(order);
    assertThat(matchResult.incomingOrder()).isEqualTo(order);
    assertThat(matchResult.orderMatches()).isEmpty();
  }
}
