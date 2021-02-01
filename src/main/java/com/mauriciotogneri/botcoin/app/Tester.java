package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.provider.FileProvider;
import com.mauriciotogneri.botcoin.provider.PriceProvider;
import com.mauriciotogneri.botcoin.strategy.BasicBuyStrategy;
import com.mauriciotogneri.botcoin.strategy.BasicSellStrategy;
import com.mauriciotogneri.botcoin.strategy.BasicStrategy;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;
import com.mauriciotogneri.botcoin.wallet.Wallet;

public class Tester
{
    public static void main(String[] args) throws Exception
    {
        FileProvider firstPriceProvider = new FileProvider("input/prices_BTCEUR_1m.csv");
        Log log = new Log("output/logs.txt");

        double maxProfit = 0;
        int bestA = 0;
        int bestB = 0;
        double bestX = 0;
        double bestY = 0;
        int index = 1;

        /*for (int a = 1; a <= 100; a += 5)
        {
            for (int b = 1; b <= 100; b += 5)
            {
                for (int x = 1; x <= 10; x += 1)
                {
                    for (int y = 1; y <= 10; y += 1)
                    {
                        System.out.println((index++ / 400f) + "%");

                        PriceProvider priceProvider = new FileProvider(firstPriceProvider.prices());
                        double totalBalance = getPrice(log, priceProvider, a, b, x, y);

                        if (totalBalance > maxProfit)
                        {
                            maxProfit = totalBalance;
                            bestA = a;
                            bestB = b;
                            bestX = x;
                            bestY = y;
                        }
                    }
                }
            }
        }*/

        maxProfit = getPrice(log, firstPriceProvider, 100, 100, 1, 4);

        System.out.println(String.format("MAX PROFIT: %s", maxProfit));
        System.out.println(String.format("BEST A: %s", bestA));
        System.out.println(String.format("BEST B: %s", bestB));
        System.out.println(String.format("BEST X: %s", bestX));
        System.out.println(String.format("BEST Y: %s", bestY));
    }

    private static double getPrice(Log log, PriceProvider priceProvider, int a, int b, int x, int y) throws Exception
    {
        double minPercentageDown = (double) x / 100f; //0.01f;
        double percentageBuyMultiplier = a; //70;
        double minEurToSpend = 10;

        double minPercentageUp = (double) y / 100f; //0.05f;
        double percentageSellMultiplier = b; //100;
        double sellAllLimit = 0.001f;
        double minEurToGain = 10;

        Currency eur = new Currency("EUR", 2);
        Currency btc = new Currency("BTC", 8);
        Balance balanceEUR = new Balance(eur, 5000);
        Balance balanceBTC = new Balance(btc, 0);
        Wallet wallet = new Wallet(balanceEUR, balanceBTC, log);
        BasicBuyStrategy buyStrategy = new BasicBuyStrategy(wallet, minPercentageDown, percentageBuyMultiplier, minEurToSpend);
        BasicSellStrategy sellStrategy = new BasicSellStrategy(wallet, minPercentageUp, percentageSellMultiplier, sellAllLimit, minEurToGain);
        Strategy strategy = new BasicStrategy(buyStrategy, sellStrategy);

        Botcoin botcoin = new Botcoin(wallet, priceProvider, strategy);

        return botcoin.start();
    }
}