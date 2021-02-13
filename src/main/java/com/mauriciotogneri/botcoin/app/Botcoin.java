package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.exchange.BinancePriceProvider;
import com.mauriciotogneri.botcoin.exchange.BinanceTrader;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.log.ProfitFile;
import com.mauriciotogneri.botcoin.log.StatusProperties;
import com.mauriciotogneri.botcoin.market.Market;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.momo.LogEvent;
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
        markets.add(market(Currency.ETH, Currency.BTC));
        markets.add(market(Currency.LTC, Currency.BTC));
        markets.add(market(Currency.ADA, Currency.BTC));

        return markets;
    }

    @NotNull
    private static Market<Price> market(Currency currencyA, Currency currencyB)
    {
        ExchangeInfo exchangeInfo = Binance.apiClient().getExchangeInfo();
        Symbol symbol = new Symbol(currencyA, currencyB, exchangeInfo);
        StatusProperties statusProperties = new StatusProperties(symbol);

        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(symbol.name);
        SymbolFilter filter = symbolInfo.getSymbolFilter(FilterType.LOT_SIZE);
        BigDecimal minQuantity = new BigDecimal(filter.getMinQty());

        DataProvider<Price> dataProvider = new BinancePriceProvider(symbol, 10);
        //DataProvider<Price> dataProvider = new FilePriceProvider(String.format("input/prices_%s%s_ONE_MINUTE.csv", currencyA.name(), currencyB.name()));

        Account account = Binance.account();

        Log.truncate(ProfitFile.path(symbol));
        Log.truncate(LogEvent.balancePath(symbol));

        BigDecimal balanceAssetA = Binance.balance(account, symbol.assetA);
        Balance balanceA = new Balance(symbol.assetA, balanceAssetA);

        BigDecimal balanceAssetB = Binance.balance(account, symbol.assetB);
        Balance balanceB = new Balance(symbol.assetB, balanceAssetB);

        Strategy<Price> strategy = new ComplexStrategy(symbol, balanceA, balanceB, minQuantity, statusProperties);

        Trader trader = new BinanceTrader();
        //Trader trader = new FakeTrader();

        Log log = new Log(String.format("output/%s/logs.json", symbol.name));

        return new Market<>(dataProvider, strategy, trader, log);
    }
}