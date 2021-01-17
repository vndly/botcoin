package com.mauriciotogneri.botcoin;

import com.mauriciotogneri.botcoin.provider.FileProvider;
import com.mauriciotogneri.botcoin.provider.PriceProvider;
import com.mauriciotogneri.botcoin.strategy.buy.BasicBuyStrategy;
import com.mauriciotogneri.botcoin.strategy.buy.BuyStrategy;
import com.mauriciotogneri.botcoin.strategy.sell.BasicSellStrategy;
import com.mauriciotogneri.botcoin.strategy.sell.SellStrategy;
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
        while (true)
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
        Wallet wallet = new BtcEurWallet(0, 0);
        PriceProvider priceProvider = new FileProvider("input/prices.csv");
        BuyStrategy buyStrategy = new BasicBuyStrategy(wallet, 10);
        SellStrategy sellStrategy = new BasicSellStrategy();

        Botcoin botcoin = new Botcoin(
                wallet,
                priceProvider,
                buyStrategy,
                sellStrategy
        );
        botcoin.start();
    }

    /*private void collectPrices() throws Exception
    {
        FileWriter fileWriter = new FileWriter("prices.csv", true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        BinanceProvider binance = new BinanceProvider("BTCEUR");

        while (true)
        {
            long timestamp = System.currentTimeMillis() / 1000;
            float price = binance.price();
            String line = String.format("%s;%s\n", timestamp, price);

            bufferedWriter.write(line);
            bufferedWriter.flush();

            Thread.sleep(1000 * 60);
        }
    }*/
}