package com.cfbenchmarks.interview.cucumber.steps;

import com.cfbenchmarks.interview.cucumber.CucumberSpringIntegration;
import com.cfbenchmarks.interview.cucumber.store.ScenarioStore;
import com.cfbenchmarks.interview.manager.OrderBookManager;
import com.cfbenchmarks.interview.model.book.OrderBookTestHelper;
import com.cfbenchmarks.interview.model.match.MatchResult;
import com.cfbenchmarks.interview.model.match.OrderMatch;
import com.cfbenchmarks.interview.model.order.Order;
import com.cfbenchmarks.interview.model.order.Side;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.groups.Tuple;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class OrderBookSteps extends CucumberSpringIntegration {

  @Autowired
  private OrderBookTestHelper orderBookTestHelper;
  @Autowired
  private OrderBookManager orderBookManager;
  @Autowired
  private ScenarioStore scenarioStore;

  @Given("an empty order book for instrument {string}")
  public void anEmptyOrderBookForInstrument(String instrument) {
    orderBookTestHelper.clearBook(instrument);
  }

  @When("the following order is added:")
  public void theFollowingOrderIsAdded(List<Order> orders) {
    Order order = orders.get(0);
    MatchResult matchResult = orderBookManager.addOrder(order);
    scenarioStore.setMatchResult(matchResult);
  }

  @Then("there are no matched orders")
  public void thereAreNoMatchedOrders() {
    MatchResult matchResult = scenarioStore.getMatchResult();
    assertThat(matchResult.orderMatches()).isEmpty();
  }

  @And("the {string} book contains:")
  public void theBookContains(String instrument, List<Order> orders) {

    List<Order> expectedBuys = orders
            .stream()
            .filter(o -> o.side() == Side.BUY)
            .toList();

    List<Order> expectedSells = orders
            .stream()
            .filter(o -> o.side() == Side.SELL)
            .toList();

    List<Order> bookBuys = orderBookTestHelper.getBuys(instrument);
    List<Order> bookSells = orderBookTestHelper.getSells(instrument);

    assertThat(bookBuys).isEqualTo(expectedBuys);
    assertThat(bookSells).isEqualTo(expectedSells);

  }

  @Given("the book contains following orders:")
  public void theBookContainsFollowingOrdersForInstrument(List<Order> orders) {
    addOrders(orders);
  }

  @Then("there the following orders are matched:")
  public void thereTheFollowingOrdersAreMatched(DataTable dataTable) {
    MatchResult matchResult = scenarioStore.getMatchResult();

    Tuple[] expectedMatches = dataTable.asMaps()
            .stream()
            .map(map -> tuple(
                    map.get("incomingOrderId"),
                    map.get("bookOrderId"),
                    Long.parseLong(map.get("price")),
                    Long.parseLong(map.get("fillQuantity"))))
            .toArray(Tuple[]::new);

    assertThat(matchResult.orderMatches())
            .extracting(
                    om -> om.incomingOrder().orderId(),
                    om -> om.bookOrder().orderId(),
                    OrderMatch::price,
                    OrderMatch::fillQuantity
            ).containsExactly(
                    expectedMatches
            );
  }

  @When("a request to delete order {string} is received")
  public void aRequestToDeleteOrderIsReceived(String orderId) {
    boolean deleteResponse = orderBookManager.deleteOrder(orderId);
    scenarioStore.setDeleteResponse(deleteResponse);
  }

  @Then("the delete command returns {string}")
  public void theDeleteCommandReturns(String expectedResponse) {
    Boolean deleteResponse = scenarioStore.getDeleteResponse();
    assertThat(deleteResponse).isEqualTo(Boolean.valueOf(expectedResponse));
  }

  @When("a best {string} price request for instrument {string} is received")
  public void aBestPriceRequestForInstrumentIsReceived(String side, String instrument) {
    Optional<Long> bestPrice = orderBookManager.getBestPrice(instrument, Side.valueOf(side));
    scenarioStore.setBestPrice(bestPrice);
  }

  @Then("the best price is empty")
  public void theBestPriceIsEmpty() {
    Optional<Long> bestPrice = scenarioStore.getBestPrice();
    assertThat(bestPrice).isEmpty();
  }

  @Then("the best price is {string}")
  public void theBestPriceIs(String expected) {
    Optional<Long> bestPrice = scenarioStore.getBestPrice();
    assertThat(bestPrice).contains(Long.parseLong(expected));
  }

  @When("the following get orders at level request is received:")
  public void theFollowingGetOrdersAtLevelRequestIsReceived(DataTable dataTable) {
    Map<String, String> map = dataTable.asMaps().get(0);

    List<Order> ordersAtLevel = orderBookManager
            .getOrdersAtLevel(map.get("instrumentId"),
                    Side.valueOf(map.get("side")),
                    Long.parseLong(map.get("priceLevel")));

    scenarioStore.setOrdersAtLevel(ordersAtLevel);
  }

  @Then("no orders are returned")
  public void noOrdersAreReturned() {
    List<Order> ordersAtLevel = scenarioStore.getOrdersAtLevel();
    assertThat(ordersAtLevel).isEmpty();
  }

  @Then("the following orders are returned:")
  public void theFollowingOrdersAreReturned(List<Order> orders) {
    List<Order> ordersAtLevel = scenarioStore.getOrdersAtLevel();
    assertThat(ordersAtLevel).isEqualTo(orders);
  }

  private void addOrders(List<Order> orders) {
    orders.forEach(order -> orderBookManager.addOrder(order));
  }


}
