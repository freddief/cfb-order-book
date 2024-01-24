package com.cfbenchmarks.interview.cucumber.store;


import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.order.Order;
import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@ScenarioScope
@Getter
@Setter
@Component
public class ScenarioStore {

  private MatchResult matchResult;
  private Boolean deleteResponse;
  private Optional<Long> bestPrice;
  private List<Order> ordersAtLevel;
}
