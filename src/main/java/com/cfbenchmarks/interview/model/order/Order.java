package com.cfbenchmarks.interview.model.order;

import lombok.With;

import static java.lang.String.format;

// TODO: change to big decimals?
public record Order(
        String orderId,
        String instrumentId,
        Side side,
        long price,
        long quantity,
        @With long filledQuantity) {

  public Order increaseFilledQuantity(Long increaseFillQuantity) {
    long remainingQuantity = remainingQuantity();
    if (increaseFillQuantity > remainingQuantity) {
      throw new IllegalArgumentException(format(
              "Cannot increase filled quantity by %s as it would exceed the remaining quantity %s",
              increaseFillQuantity,
              remainingQuantity));
    }
    long totalFilledQuantity = filledQuantity + increaseFillQuantity;
    return withFilledQuantity(totalFilledQuantity);
  }

  public long remainingQuantity() {
    return quantity - filledQuantity;
  }

  public boolean isFilled() {
    return remainingQuantity() == 0;
  }


}
