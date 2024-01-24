# CFB Order Book

Tech test for CFBenchmarks. Requirements found in [instructions.md](instructions.md) 

## Build
`mvn clean install`

## Structure
* Main sources: [src/main/java](src/main/java)
* Test sources: [src/test/java](src/test/java)
* BDD features: [src/test/resources](src/test/resources)

## Implementation
* Implements contracts of [OrderBookManager](src/main/java/com/cfbenchmarks/interview/manager/OrderBookManager.java)
* Uses mutable data structures to manage order book state:
  * [OrderBook](src/main/java/com/cfbenchmarks/interview/model/book/OrderBook.java)
  * [InstrumentOrderBook](src/main/java/com/cfbenchmarks/interview/model/book/InstrumentOrderBook.java)
* Manages concurrent access by synchronising access to an instrument's book using a [ConcurrentHashMap](src/main/java/com/cfbenchmarks/interview/model/book/OrderBook.java)
* Implements relevant unit and BDD tests