Feature: Best Price

  Scenario: Empty order book
    Given an empty order book for instrument "BTC-GBP"
    When a best "BUY" price request for instrument "BTC-GBP" is received
    Then the best price is empty

  Scenario: Best buy price
    Given the book contains following orders:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | SELL | 16    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | BUY  | 14    | 10       | 0              |
      | bookOrder3 | BTC-GBP      | BUY  | 13    | 10       | 0              |
      | bookOrder4 | BTC-GBP      | BUY  | 12    | 10       | 0              |
    When a best "BUY" price request for instrument "BTC-GBP" is received
    Then the best price is "14"

  Scenario: Best sell price
    Given the book contains following orders:
      | orderId    | instrumentId | side | price | quantity | filledQuantity |
      | bookOrder1 | BTC-GBP      | BUY  | 11    | 10       | 0              |
      | bookOrder2 | BTC-GBP      | SELL | 12    | 10       | 0              |
      | bookOrder3 | BTC-GBP      | SELL | 15    | 10       | 0              |
      | bookOrder4 | BTC-GBP      | SELL | 16    | 10       | 0              |
    When a best "SELL" price request for instrument "BTC-GBP" is received
    Then the best price is "12"



