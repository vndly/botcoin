package com.mauriciotogneri.botcoin;

import com.mauriciotogneri.botcoin.providers.FileProvider;
import com.mauriciotogneri.botcoin.providers.PriceProvider;
import com.mauriciotogneri.botcoin.strategies.buy.BasicBuyStrategy;
import com.mauriciotogneri.botcoin.strategies.buy.BuyStrategy;
import com.mauriciotogneri.botcoin.strategies.sell.BasicSellStrategy;
import com.mauriciotogneri.botcoin.strategies.sell.SellStrategy;

public class Botcoin
{
    public static void main(String[] args) throws Exception
    {
        PriceProvider provider = new FileProvider("input/prices.csv");
        BuyStrategy buyStrategy = new BasicBuyStrategy();
        SellStrategy sellStrategy = new BasicSellStrategy();

        while (true)
        {
            float price = provider.price();

            float buyAmount = buyStrategy.buy();
            float sellAmount = sellStrategy.sell();


        }
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