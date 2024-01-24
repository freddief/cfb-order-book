Feature: Delete Order

  Scenario: Empty order book
    Given the book contains following orders:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | SELL | 12    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | SELL | 13    | 10       | 0              |
    When a request to delete order "nonExistentOrder" is received
    Then the delete command returns "false"
    And the "BTC-GBP" book contains:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | SELL | 12    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | SELL | 13    | 10       | 0              |

  Scenario: Order exists
    Given the book contains following orders:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | SELL | 12    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | SELL | 13    | 10       | 0              |
    When a request to delete order "bookOrder2" is received
    Then the delete command returns "true"
    And the "BTC-GBP" book contains:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | SELL | 12    | 10       | 0              |
