package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.exchange.BinanceCandlePriceProvider;
import com.mauriciotogneri.botcoin.mellau.candle.CandleStrategy;
import com.mauriciotogneri.botcoin.mellau.candle.dto.RequestDataDTO;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.FakeTrader;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;


public class MellauCandle {
    public static void main(String[] args) throws Exception {
        DataProvider<RequestDataDTO> dataProvider = new BinanceCandlePriceProvider("BTCBUSD");

        Balance balanceEUR = new Balance(Currency.EUR, "20");
        Balance balanceBTC = new Balance(Currency.BTC, "0");

        Strategy<RequestDataDTO> strategy = new CandleStrategy(balanceEUR, balanceBTC);

        Trader trader = new FakeTrader();
        // Trader trader = new BinanceTrader();

        Log log = new Log("output/logs.json");

        Botcoin<Price> botcoin = new Botcoin(dataProvider, strategy, trader, log);
        botcoin.start();
    }
}
