package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolInfo;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.exchange.BinancePriceProvider;
import com.mauriciotogneri.botcoin.exchange.BinanceTrader;
import com.mauriciotogneri.botcoin.log.ConfigFile;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.log.ProfitFile;
import com.mauriciotogneri.botcoin.market.Market;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.momo.ComplexStrategy;
import com.mauriciotogneri.botcoin.momo.LogEvent;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.FilePriceProvider;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.FakeTrader;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Botcoin
{
    public static final Boolean TEST_MODE = Boolean.parseBoolean(System.getenv("TEST_MODE"));
    private static final Integer FREQUENCY = 10; // in seconds

    public static void main(String[] args)
    {
        Log.truncate("account.txt");
        Log.truncate("error.txt");
        Log.truncate("console.txt");

        Log log = new Log("start.txt");
        log.write(LocalDateTime.now().toString());
        log.close();

        List<Market<?>> markets = markets();
        int sleepTime = (1000 * FREQUENCY) / markets.size();

        for (Market<?> market : markets)
        {
            Thread thread = new Thread(market);
            thread.start();

            try
            {
                Thread.sleep(sleepTime);
            }
            catch (Exception e)
            {
                Log.error(e);
            }
        }
    }

    @NotNull
    private static List<Market<?>> markets()
    {
        ExchangeInfo exchangeInfo = Binance.apiClient().getExchangeInfo();
        Account account = Binance.account();

        File[] symbols = new File("output").listFiles();
        List<Market<?>> markets = new ArrayList<>();

        if (symbols != null)
        {
            for (File symbol : symbols)
            {
                Currency[] currencies = Currency.currencies(symbol.getName());
                markets.add(market(exchangeInfo, account, currencies[0], currencies[1]));
            }
        }

        return markets;
    }

    @NotNull
    private static Market<Price> market(ExchangeInfo exchangeInfo, Account account, Currency currencyA, Currency currencyB)
    {
        Symbol symbol = new Symbol(currencyA, currencyB, exchangeInfo);
        ConfigFile configFile = new ConfigFile(symbol);

        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(symbol.name);
        BigDecimal minQuantity = new BigDecimal(symbolInfo.getSymbolFilter(FilterType.LOT_SIZE).getMinQty());
        BigDecimal minNotional = new BigDecimal(symbolInfo.getSymbolFilter(FilterType.MIN_NOTIONAL).getMinNotional());

        DataProvider<Price> dataProvider = TEST_MODE ?
                new FilePriceProvider(String.format("input/prices_%s%s_ONE_MINUTE.csv", currencyA.name(), currencyB.name())) :
                new BinancePriceProvider(symbol, FREQUENCY);

        Trader trader = TEST_MODE ?
                new FakeTrader() :
                new BinanceTrader();

        Log.truncate(ProfitFile.path(symbol));
        Log.truncate(LogEvent.balancePath(symbol));

        BigDecimal balanceAssetA = Binance.balance(account, symbol.assetA);
        Balance balanceA = new Balance(symbol.assetA, balanceAssetA);

        BigDecimal balanceAssetB = Binance.balance(account, symbol.assetB);
        Balance balanceB = new Balance(symbol.assetB, balanceAssetB);

        Strategy<Price> strategy = new ComplexStrategy(symbol, balanceA, balanceB, minQuantity, minNotional, configFile);

        Log log = new Log(String.format("output/%s/logs.json", symbol.name));

        return new Market<>(dataProvider, strategy, trader, log);
    }
}