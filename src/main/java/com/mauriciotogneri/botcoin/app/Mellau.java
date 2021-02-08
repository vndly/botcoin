package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.market.Candlestick;
import com.mauriciotogneri.botcoin.exchange.BinanceCandlePriceProvider;
import com.mauriciotogneri.botcoin.mellau.basic.CrossStrategy;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.FakeTrader;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;

import java.util.List;

public class Mellau {
    public static void main(String[] args) throws Exception {
        // DataProvider<Price> dataProvider = new FilePriceProvider("input/prices_BTCEUR_1m.csv");
        DataProvider<List<Candlestick>> dataProvider = new BinanceCandlePriceProvider("BTCEUR", 10);

        Balance balanceEUR = new Balance(Currency.EUR, "10");
        Balance balanceBTC = new Balance(Currency.BTC, "0");

        Strategy<List<Price>> strategy = new CrossStrategy(balanceEUR, balanceBTC);

        Trader trader = new FakeTrader();
        // Trader trader = new BinanceTrader();

        Log log = new Log("output/logs.json");

        Botcoin<Price> botcoin = new Botcoin(dataProvider, strategy, trader, log);
        botcoin.start();
    }
}
