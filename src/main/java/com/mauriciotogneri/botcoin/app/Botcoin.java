package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.provider.FileProvider;
import com.mauriciotogneri.botcoin.provider.PriceProvider;
import com.mauriciotogneri.botcoin.strategy.buy.BasicBuyStrategy;
import com.mauriciotogneri.botcoin.strategy.buy.BuyStrategy;
import com.mauriciotogneri.botcoin.strategy.sell.BasicSellStrategy;
import com.mauriciotogneri.botcoin.strategy.sell.SellStrategy;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.BtcEurWallet;
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
        Log log = new Log("output/logs.txt");
        BtcEurWallet wallet = new BtcEurWallet(1000, 0, log);
        PriceProvider priceProvider = new FileProvider("input/prices.csv");
        BuyStrategy buyStrategy = new BasicBuyStrategy(wallet, 10);
        SellStrategy sellStrategy = new BasicSellStrategy(wallet, 10);

        Botcoin botcoin = new Botcoin(
                wallet,
                priceProvider,
                buyStrategy,
                sellStrategy
        );
        botcoin.start();
    }
}