package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.general.ExchangeInfo;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.exchange.BinanceCandlePriceProvider;
import com.mauriciotogneri.botcoin.exchange.BinanceCrossPriceProvider;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.market.Market;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.mellau.candle.CandleStrategy;
import com.mauriciotogneri.botcoin.mellau.candle.dto.RequestDataDTO;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.FakeTrader;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;

import java.math.BigDecimal;


public class MellauCandle {
    public static void main(String[] args) throws Exception {
        ExchangeInfo exchangeInfo = Binance.apiClient().getExchangeInfo();
        Symbol symbol = new Symbol(Currency.BTC, Currency.EUR, exchangeInfo);

        DataProvider<RequestDataDTO> dataProvider = new BinanceCrossPriceProvider(symbol.name);

        Balance balanceEUR = new Balance(symbol.assetB, new BigDecimal("20"));//Currency.EUR
        Balance balanceBTC = new Balance(symbol.assetA, new BigDecimal("0"));

        Strategy<RequestDataDTO> strategy = new CandleStrategy(balanceEUR, balanceBTC, symbol);

        Trader trader = new FakeTrader();
        // Trader trader = new BinanceTrader();


        Log log = new Log("output/logs.json");

        Market<RequestDataDTO> market = new Market<>(dataProvider, strategy, trader, log);
        market.run();
    }
}
