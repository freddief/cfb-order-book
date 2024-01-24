package com.cfbenchmarks.interview.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.cfbenchmarks.interview.matching.MatchingEngine;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;
import com.cfbenchmarks.interview.operation.AddOrderOperation;
import com.cfbenchmarks.interview.operation.DeleteOrderOperation;
import com.cfbenchmarks.interview.operation.GetBestPriceOperation;
import com.cfbenchmarks.interview.operation.GetOrdersAtLevelOperation;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookOperationFactoryTest {

  @Mock private MatchingEngine matchingEngine;

  @InjectMocks private BookOperationFactory underTest;

  @Test
  void addOrderOperation() {
    Order order = mock(Order.class);
    AtomicReference<MatchResult> result = new AtomicReference<>();

    AddOrderOperation returned = underTest.addOrderOperation(order, result);

    assertThat(returned).isNotNull();
    assertThat(returned).isInstanceOf(AddOrderOperation.class);
  }

  @Test
  void deleteOrderOperation() {
    String orderId = "orderId";
    AtomicBoolean result = new AtomicBoolean();

    DeleteOrderOperation returned = underTest.deleteOrderOperation(orderId, result);

    assertThat(returned).isNotNull();
    assertThat(returned).isInstanceOf(DeleteOrderOperation.class);
  }

  @Test
  void getBestPriceOperation() {
    Side side = Side.BUY;
    AtomicReference<Optional<Long>> result = new AtomicReference<>();

    GetBestPriceOperation returned = underTest.getBestPriceOperation(side, result);

    assertThat(returned).isNotNull();
    assertThat(returned).isInstanceOf(GetBestPriceOperation.class);
  }

  @Test
  void getOrdersAtLevelOperation() {
    Side side = Side.BUY;
    long price = 123L;
    AtomicReference<List<Order>> result = new AtomicReference<>();

    GetOrdersAtLevelOperation returned = underTest.getOrdersAtLevelOperation(side, price, result);

    assertThat(returned).isNotNull();
    assertThat(returned).isInstanceOf(GetOrdersAtLevelOperation.class);
  }
}
