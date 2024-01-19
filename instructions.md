# Orderbook Test

An Order book is used by an exchange to maintain and match a list of orders from buyers and sellers. Order books for different instruments are separate.

Each order book consists of two sides – bid and ask. The bid side is the set of orders from buyers and the ask side is the set of orders from sellers.

Each side consists of price levels, where orders from each side of the book with the same price are grouped together.

The levels within the book side are sorted by price in such way that the top level for the bid side contains orders with the highest price, and the top level for the ask side contains orders with the lowest price.

Inside each level, orders are sorted in the order as they arrive. Possible operations on the order book:
- Add new order. New order specifies instrument, side (either buy or sell), requested quantity and price. Additionally, each order is identified by a unique order ID.
- Delete existing order. You need to specify the unique order ID of an existing order. The order is removed from the order book completely.

Additionally, at any time, an order book should provide the following information:
- Best bid and best ask prices
- List of orders on a level and side of the book, in the correct order

Your task is to implement OrderBookManager, which maintains order books for different instruments.

An order book should be kept in the described order at all times, i.e. the described order should be maintained after any new/delete command, not only after receiving all orders.

When an order is added, it may potentially be matched with orders on the other side. This happens when there are orders on the other side that cause the best bid to be greater or equal to the best ask. If so, the orders on both sides are progressively removed until the best bid is less than the best ask. Therefore, the same unit of instruments is removed from both sides. Orders shall be fully or partially removed by described order. When this happens, any order that was partially matched (some, but not all of the quantity that was removed) will retain the order ID of the original order and shall not be deemed to have re-entered the orderbook if it had existed in the order book prior to matching. You do not have to report the matching progress, only the final state of the matching.

Your solution will be assessed for clarity of implementation, simplicity and readability of the code.
We will also be looking for proper usage of data structures, in particular with respect to their spatial and temporal complexity in the context of the implementation.   

Please feel free to submit any or all of your test coverage along with the implementation.

Good Luck!