package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.exchange.BinanceMellauPriceProvider;
import com.mauriciotogneri.botcoin.exchange.BinanceTrader;
import com.mauriciotogneri.botcoin.exchange.DataProviderSleepTime;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.market.Market;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.mellau.candle.CandleStrategy;
import com.mauriciotogneri.botcoin.mellau.candle.dto.RequestDataDTO;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;

import java.math.BigDecimal;

public class MellauCandle {
    public static void main(String[] args) {
        ExchangeInfo exchangeInfo = Binance.apiClient().getExchangeInfo();
        Symbol symbol = new Symbol(Currency.BNB, Currency.BTC, exchangeInfo);
        Account account = Binance.account();

        DataProviderSleepTime dataProviderSleepTime = new DataProviderSleepTime(60 * 1000);

        DataProvider<RequestDataDTO> dataProvider = new BinanceMellauPriceProvider(symbol.name, dataProviderSleepTime);

        BigDecimal balanceAssetA = Binance.balance(account, symbol.assetA).multiply(new BigDecimal("0.15"));
        Balance balanceA = new Balance(symbol.assetA, balanceAssetA); // new Balance(symbol.assetB, new BigDecimal("20"));

        BigDecimal balanceAssetB = Binance.balance(account, symbol.assetB);
        Balance balanceB = new Balance(symbol.assetB, balanceAssetB); // new Balance(symbol.assetA, new BigDecimal("0"));

        Strategy<RequestDataDTO> strategy = new CandleStrategy(balanceA, balanceB, symbol, dataProviderSleepTime);

        // Trader trader = new FakeTrader();
         Trader trader = new BinanceTrader();

        Log log = new Log("output/logs.json");

        Market<RequestDataDTO> market = new Market<>(dataProvider, strategy, trader, log);
        market.run();
    }
}
