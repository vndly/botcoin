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

    public float start() throws Exception
    {
        float lastPrice = 0;

        while (priceProvider.hasMorePrices())
        {
            float price = priceProvider.price();
            float buyAmount = buyStrategy.buy(price);
            float sellAmount = sellStrategy.sell(price);

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
        FileProvider firstPriceProvider = new FileProvider("input/prices.csv");
        Log log = new Log("output/logs.txt");

        double maxProfit = 0;
        int bestI = 0;
        int bestJ = 0;
        float bestX = 0;

        PriceProvider priceProvider = new FileProvider(firstPriceProvider.prices());

        float minPercentageDown = 0.01f;
        float percentageBuyMultiplier = 70;
        float minEurToSpend = 10;

        float minPercentageUp = 0.05f;
        float percentageSellMultiplier = 100;
        float sellAllLimit = 0.001f;
        float minEurToGain = 10;

        BasicWallet wallet = new BasicWallet(5000, 0, log);
        BuyStrategy buyStrategy = new BasicBuyStrategy(wallet, minPercentageDown, percentageBuyMultiplier, minEurToSpend);
        SellStrategy sellStrategy = new BasicSellStrategy(wallet, minPercentageUp, percentageSellMultiplier, sellAllLimit, minEurToGain);

        Botcoin botcoin = new Botcoin(
                wallet,
                priceProvider,
                buyStrategy,
                sellStrategy
        );
        float totalBalance = botcoin.start();

        if (totalBalance > maxProfit)
        {
            maxProfit = totalBalance;
            //bestI = i;
            //bestJ = j;
            // bestX = x;
        }

        System.out.println(String.format("MAX PROFIT %s %s %s %s", maxProfit, bestI, bestJ, bestX));
    }
}