# Botcoin
A crypto trading bot.

## Environment variables
* BINANCE_API
* BINANCE_SECRET

## Logs
* config.properties:
```properties
MODE=running/shutdown/stopped
SPENT=0.12345678
BOUGHT=0.87654321
```
* profit.txt:
```text
0.00024224
```
* logs.json
```json
{
}
```
* last_operation.properties:
```properties
type=buy
quantity=0.00600000 ETH
price=0.03705166 BTC
spent=0.00022231 BTC
gained=0.00001234 BTC
profit=0.00000567 BTC
boughtPrice=0.03705166 BTC
balanceA=0.00696900 ETH
balanceB=0.00181191 BTC
total=0.00187904 BTC
```
* status.properties:
```properties
allTimeHigh=0.03473350
boughtPrice=0.03407800
currentPrice=0.03194200
percentage=-6.26%
balanceA=2.97700000 ADA
balanceB=0.29785479 BTC
timestamp=1613252078103
```

---

Symbol: BASE_ASSET-QUOTE_ASSET

Example: BNB-BTC

Price: The cost of buying 1 unit of base asset, expressed in the quote asset currency

Example: 0.00438292
You need to pay 0.00438292 BTC to buy 1 BNB
You will get 0.00438292 BTC by selling 1 BNB

---

Buy: Sell quote asset to buy base asset

Example:
* I have 0 BNB and 0.5 BTC
* The current price is 0.00438292
* I buy 0.1 BNB for 0.00043829 BTC
* NewOrder.marketBuy("BNBBTC", "0.1")
* Then I have 0.1 BNB and 0.49956171 BTC (fees not taken into account)

Sell: Buy quote asset by selling base asset

Example:
* I have 0.1 BNB and 0.49956171 BTC
* The current price is 0.00442674
* I sell 0.1 BNB for 0.00044267 BTC
* NewOrder.marketSell("BNBBTC", "0.1")
* Then I have 0 BNB and 0.50000438 BTC (fees not taken into account)

---

Sell:
* I have 1 BNB and 0 BTC
* The current price is 0.00442674
* I sell 1 BNB for 0.00442674 BTC
* NewOrder.marketSell("BNBBTC", "1")
* Then I have 0 BNB and 0.00442674 BTC (fees not taken into account)

Buy:
* I have 0 BNB and 0.00442674 BTC
* The current price is 0.00438292
* I buy 1.009997901 BNB for 0.00442674 BTC
* NewOrder.marketBuy("BNBBTC", "1.009997901")
* Then I have 1.009997901 BNB and 0 BTC (fees not taken into account)