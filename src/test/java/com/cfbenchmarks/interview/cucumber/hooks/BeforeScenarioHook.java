package com.cfbenchmarks.interview.cucumber.hooks;

import com.cfbenchmarks.interview.cucumber.CucumberSpringIntegration;
import com.cfbenchmarks.interview.model.book.OrderBookTestHelper;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class BeforeScenarioHook extends CucumberSpringIntegration {

  @Autowired
  private OrderBookTestHelper orderBookTestHelper;

  @Before
  public void before() {
    orderBookTestHelper.clearBooks();
  }
}
