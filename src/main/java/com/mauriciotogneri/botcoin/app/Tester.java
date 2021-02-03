package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.FilePriceProvider;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.BasicBuyStrategy;
import com.mauriciotogneri.botcoin.strategy.BasicSellStrategy;
import com.mauriciotogneri.botcoin.strategy.BasicStrategy;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.BinanceTrader;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;
import com.mauriciotogneri.botcoin.wallet.Wallet;

public class Tester
{
    public static void main(String[] args) throws Exception
    {
        double minPercentageDown = 0.01;
        double percentageBuyMultiplier = 70;
        double minEurToSpend = 10;

        double minPercentageUp = 0.05;
        double percentageSellMultiplier = 100;
        double sellAllLimit = 0.001f;
        double minEurToGain = 10;

        Balance balanceEUR = new Balance(Currency.EUR, 5000);
        Balance balanceBTC = new Balance(Currency.BTC, 0);
        Wallet wallet = new Wallet(balanceEUR, balanceBTC);

        DataProvider<Price> dataProvider = new FilePriceProvider("input/prices_BTCEUR_1m.csv");

        BasicBuyStrategy buyStrategy = new BasicBuyStrategy(wallet, minPercentageDown, percentageBuyMultiplier, minEurToSpend);
        BasicSellStrategy sellStrategy = new BasicSellStrategy(wallet, minPercentageUp, percentageSellMultiplier, sellAllLimit, minEurToGain);
        Strategy<Price> strategy = new BasicStrategy(buyStrategy, sellStrategy);

        Trader trader = new BinanceTrader();

        Log log = new Log("output/logs.json");

        Botcoin<Price> botcoin = new Botcoin<>(wallet, dataProvider, strategy, trader, log);
        botcoin.start();
    }
}