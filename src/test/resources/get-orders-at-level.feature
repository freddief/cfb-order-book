Feature: Get Orders At Level

  Scenario: Empty order book
    Given an empty order book for instrument "BTC-GBP"
    When the following get orders at level request is received:
      | instrumentId | side | priceLevel |
      | BTC-GBP      | BUY  | 11         |
    Then no orders are returned

  Scenario: Orders exist for price level
    Given the book contains following orders:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | BUY  | 11    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | BUY  | 11    | 10       | 0              |
      | bookOrder3 | BTC-GBP      | BUY  | 12    | 10       | 0              |
      | bookOrder4 | BTC-GBP      | SELL | 9     | 10       | 0              |
    When the following get orders at level request is received:
      | instrumentId | side | priceLevel |
      | BTC-GBP      | BUY  | 11         |
    Then the following orders are returned:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | BUY  | 11    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | BUY  | 11    | 10       | 0              |
