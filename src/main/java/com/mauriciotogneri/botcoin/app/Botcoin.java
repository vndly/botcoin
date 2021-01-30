package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.provider.FileProvider;
import com.mauriciotogneri.botcoin.provider.PriceProvider;
import com.mauriciotogneri.botcoin.strategy.buy.BasicBuyStrategy;
import com.mauriciotogneri.botcoin.strategy.buy.BuyStrategy;
import com.mauriciotogneri.botcoin.strategy.sell.BasicSellStrategy;
import com.mauriciotogneri.botcoin.strategy.sell.SellStrategy;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.BasicWallet;
import com.mauriciotogneri.botcoin.wallet.Wallet;

// TODO: https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md
public class Botcoin
{
    private final Wallet wallet;
    private final PriceProvider priceProvider;
    private final BuyStrategy buyStrategy;
    private final SellStrategy sellStrategy;

    public Botcoin(Wallet wallet, PriceProvider priceProvider, BuyStrategy buyStrategy, SellStrategy sellStrategy)
    {
        this.wallet = wallet;
        this.priceProvider = priceProvider;
        this.buyStrategy = buyStrategy;
        this.sellStrategy = sellStrategy;
    }

    public double start() throws Exception
    {
        double lastPrice = 0;

        while (priceProvider.hasMorePrices())
        {
            double price = priceProvider.price();
            double buyAmount = buyStrategy.buy(price);
            double sellAmount = sellStrategy.sell(price);

            if (buyAmount > 0)
            {
                wallet.buy(buyAmount, price);
            }
            else if (sellAmount > 0)
            {
                wallet.sell(sellAmount, price);
            }

            lastPrice = price;
        }

        return wallet.totalBalance(lastPrice);
    }

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

        BasicWallet wallet = new BasicWallet(5000, 0, log);
        BuyStrategy buyStrategy = new BasicBuyStrategy(wallet, minPercentageDown, percentageBuyMultiplier, minEurToSpend);
        SellStrategy sellStrategy = new BasicSellStrategy(wallet, minPercentageUp, percentageSellMultiplier, sellAllLimit, minEurToGain);

        Botcoin botcoin = new Botcoin(
                wallet,
                priceProvider,
                buyStrategy,
                sellStrategy
        );

        return botcoin.start();
    }
}