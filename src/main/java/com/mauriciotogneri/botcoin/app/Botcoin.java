package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.exchange.BinancePriceProvider;
import com.mauriciotogneri.botcoin.exchange.BinanceTrader;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.market.Market;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.momo.complex.ComplexStrategy;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Botcoin
{
    public static void main(String[] args)
    {
        for (Market<?> market : markets())
        {
            Thread thread = new Thread(market);
            thread.start();
        }
    }

    @NotNull
    private static List<Market<?>> markets()
    {
        List<Market<?>> markets = new ArrayList<>();
        markets.add(market(new Symbol(Currency.ETH, Currency.BTC)));

        return markets;
    }

    @NotNull
    private static Market<Price> market(@NotNull Symbol symbol)
    {
        ExchangeInfo exchangeInfo = Binance.apiClient().getExchangeInfo();
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(symbol.toString());
        SymbolFilter filter = symbolInfo.getSymbolFilter(FilterType.LOT_SIZE);
        BigDecimal minQuantity = new BigDecimal(filter.getMinQty());

        DataProvider<Price> dataProvider = new BinancePriceProvider(symbol, 10);

        BigDecimal balanceAssetA = Binance.balance(symbol.currencyA);
        Balance balanceA = new Balance(symbol.currencyA, balanceAssetA);

        BigDecimal balanceAssetB = Binance.balance(symbol.currencyB);
        Balance balanceB = new Balance(symbol.currencyB, balanceAssetB);

        Strategy<Price> strategy = new ComplexStrategy(balanceA, balanceB);

        Trader trader = new BinanceTrader();

        Log log = new Log(String.format("output/logs_%s.json", symbol));

        return null; // new Market<>(dataProvider, strategy, trader, log);
    }

    /*private static void runBot() throws Exception
    {
        String minEurToTrade = "10";
        String minBtcToTrade = "0.0005";

        String minPercentageDown = "0.01";
        String percentageBuyMultiplier = "50";

        String minPercentageUp = "0.02";
        String percentageSellMultiplier = "100";
        String sellAllLimit = "0.001";

        //DataProvider<Price> dataProvider = new FilePriceProvider("input/prices_BTCEUR_1m.csv");
        DataProvider<Price> dataProvider = new BinancePriceProvider("BTCEUR", 10);

        Balance balanceEUR = new Balance(Currency.EUR, "94.42730924");
        Balance balanceBTC = new Balance(Currency.BTC, "0");
        Strategy<Price> strategy = new BasicStrategy(balanceEUR,
                                                     balanceBTC,
                                                     minPercentageDown,
                                                     percentageBuyMultiplier,
                                                     minPercentageUp,
                                                     percentageSellMultiplier,
                                                     sellAllLimit,
                                                     minEurToTrade,
                                                     minBtcToTrade);

        //Trader trader = new FakeTrader();
        Trader trader = new BinanceTrader();

        Log log = new Log("output/logs.json");

        Botcoin<Price> botcoin = new Botcoin<>(dataProvider, strategy, trader, log);
        botcoin.start();
    }*/
}