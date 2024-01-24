Feature: Add Order

  Scenario: Empty order book
    Given an empty order book for instrument "BTC-GBP"
    When the following order is added:
      | orderId  | instrumentId | side | price | quantity |
      | orderId1 | BTC-GBP      | BUY  | 12    | 10       |
    Then there are no matched orders
    And the "BTC-GBP" book contains:
      | orderId  | instrumentId | side | price | quantity | filledQuantity |
      | orderId1 | BTC-GBP      | BUY  | 12    | 10       | 0              |

  Scenario: Order does not cross the book
    Given the book contains following orders:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | SELL | 12    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | SELL | 13    | 10       | 0              |
      | bookOrder3 | BTC-GBP      | SELL | 15    | 10       | 0              |
    When the following order is added:
      | orderId         | instrumentId | side | price | quantity |
      | incomingOrderId | BTC-GBP      | BUY  | 11    | 10       |
    Then there are no matched orders
    And the "BTC-GBP" book contains:
      | orderId         | instrumentId | side | price | quantity | filledQuantity |
      | incomingOrderId | BTC-GBP      | BUY  | 11    | 10       | 0              |
      | bookOrder1      | BTC-GBP      | SELL | 12    | 10       | 0              |
      | bookOrder2      | BTC-GBP      | SELL | 13    | 10       | 0              |
      | bookOrder3      | BTC-GBP      | SELL | 15    | 10       | 0              |

  Scenario: Order fully filled
    Given the book contains following orders:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | SELL | 12    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | SELL | 13    | 10       | 0              |
      | bookOrder3 | BTC-GBP      | SELL | 15    | 10       | 0              |
    When the following order is added:
      | orderId         | instrumentId | side | price | quantity |
      | incomingOrderId | BTC-GBP      | BUY  | 13    | 15       |
    Then there the following orders are matched:
      | incomingOrderId | bookOrderId | price | fillQuantity |
      | incomingOrderId | bookOrder1  | 12    | 10           |
      | incomingOrderId | bookOrder2  | 13    | 5            |
    And the "BTC-GBP" book contains:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder2 | BTC-GBP      | SELL | 13    | 10       | 5              |
      | bookOrder3 | BTC-GBP      | SELL | 15    | 10       | 0              |

  Scenario: Order partially filled
    Given the book contains following orders:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | SELL | 12    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | SELL | 13    | 10       | 0              |
      | bookOrder3 | BTC-GBP      | SELL | 15    | 10       | 0              |
    When the following order is added:
      | orderId         | instrumentId | side | price | quantity |
      | incomingOrderId | BTC-GBP      | BUY  | 13    | 30       |
    Then there the following orders are matched:
      | incomingOrderId | bookOrderId | price | fillQuantity |
      | incomingOrderId | bookOrder1  | 12    | 10           |
      | incomingOrderId | bookOrder2  | 13    | 10           |
    And the "BTC-GBP" book contains:
      | orderId         | instrumentId | side | price | quantity | filledQuantity |
      | incomingOrderId | BTC-GBP      | BUY  | 13    | 30       | 20             |
      | bookOrder3      | BTC-GBP      | SELL | 15    | 10       | 0              |