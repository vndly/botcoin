package com.mauriciotogneri.botcoin.app;

public class MellauCross {
    public static void main(String[] args) {
        /*ExchangeInfo exchangeInfo = Binance.apiClient().getExchangeInfo();
        Symbol symbol = new Symbol(Currency.BTC, Currency.EUR, exchangeInfo);

        DataProvider<RequestDataDTO> dataProvider = new BinanceCrossPriceProvider(symbol.name);

        Balance balanceEUR = new Balance(symbol.assetB, new BigDecimal("20"));//Currency.EUR
        Balance balanceBTC = new Balance(symbol.assetA, new BigDecimal("0"));

        Strategy<RequestDataDTO> strategy = new CrossStrategy(balanceEUR, balanceBTC);

        Trader trader = new FakeTrader();
        // Trader trader = new BinanceTrader();

        Log log = new Log("output/logs.json");

        Market<RequestDataDTO> market = new Market<>(dataProvider, strategy, trader, log);
        market.run();*/
    }
}
