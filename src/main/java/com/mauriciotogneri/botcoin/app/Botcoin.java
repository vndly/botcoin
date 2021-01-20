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

    public void start() throws Exception
    {
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
        }
    }

    public static void main(String[] args) throws Exception
    {
        PriceProvider priceProvider = new FileProvider("input/prices.csv");
        Log log = new Log("output/logs.txt");

        float minEurThreshold = 0;
        float minPercentageThreshold = 0.01f;
        float percentageMultiplier = 1;

        BasicWallet wallet = new BasicWallet(5000, 0, log);
        BuyStrategy buyStrategy = new BasicBuyStrategy(wallet, minEurThreshold, minPercentageThreshold, percentageMultiplier);
        SellStrategy sellStrategy = new BasicSellStrategy(wallet, minEurThreshold, minPercentageThreshold, percentageMultiplier);

        Botcoin botcoin = new Botcoin(
                wallet,
                priceProvider,
                buyStrategy,
                sellStrategy
        );
        botcoin.start();
    }
}