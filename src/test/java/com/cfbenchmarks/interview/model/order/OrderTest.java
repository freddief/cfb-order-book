package com.cfbenchmarks.interview.model.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class OrderTest {

  @Test
  void increaseFilledQuantity() {
    long quantity = 100L;
    long filledQuantity = 20L;
    Order order = order(quantity, filledQuantity);

    Order returned = order.increaseFilledQuantity(40L);

    assertThat(returned.filledQuantity()).isEqualTo(60);
  }

  @Test
  void increaseFilledQuantity_whenFillQuantityExceedsRemaining_thenThrowException() {
    long quantity = 100L;
    long filledQuantity = 20L;
    Order order = order(quantity, filledQuantity);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> order.increaseFilledQuantity(81L));

    assertThat(exception)
        .hasMessage(
            "Cannot increase filled quantity by 81 as it would exceed the remaining quantity 80");
  }

  @Test
  void remainingQuantity() {
    long quantity = 100L;
    long filledQuantity = 20L;
    Order order = order(quantity, filledQuantity);

    long remainingQuantity = order.remainingQuantity();

    assertThat(remainingQuantity).isEqualTo(80L);
  }

  @Test
  void isFilled() {
    long quantity = 100L;
    long filledQuantity = 100L;
    long quantity2 = 100L;
    long filledQuantity2 = 20L;

    Order order = order(quantity, filledQuantity);
    Order order2 = order(quantity2, filledQuantity2);

    boolean filledOrderReturn = order.isFilled();
    boolean notFilledOrderReturn = order2.isFilled();

    assertThat(filledOrderReturn).isTrue();
    assertThat(notFilledOrderReturn).isFalse();
  }

  private Order order(long quantity, long filledQuantity) {
    return new Order("orderId", "instrumentId", Side.BUY, 123L, quantity, filledQuantity);
  }
}
