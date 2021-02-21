# Botcoin
A crypto trading bot.

## Environment variables
* BINANCE_API
* BINANCE_SECRET

## Logs
* config.properties:
```properties
MODE=on/off/shutdown
STATUS=BUYING/SELLING
BOUGHT_PRICE=0.00000000
```
* profit.txt:
```text
0.00024224154
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

* price.properties:
```properties
state=SELLING
allTimeHigh=0.00000000
boughtPrice=0.00001981
currentPrice=0.00001879
percentage=-5.14%
```