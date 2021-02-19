package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import com.mauriciotogneri.botcoin.market.Symbol;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.Price;

import java.math.BigDecimal;

public class BinancePriceProvider implements DataProvider<Price>
{
    private Price lastPrice;
    private final Symbol symbol;
    private final int frequency;
    private final BinanceApiRestClient client;

    public BinancePriceProvider(Symbol symbol, int frequency)
    {
        this.symbol = symbol;
        this.frequency = frequency;
        this.client = Binance.apiClient();
    }

    @Override
    public boolean hasData()
    {
        return true;
    }

    @Override
    public Price data()
    {
        try
        {
            Thread.sleep(frequency * 1000L);
            TickerPrice tickerPrice = client.getPrice(symbol.name);

            lastPrice = new Price(
                    System.currentTimeMillis(),
                    new BigDecimal(tickerPrice.getPrice())
            );

            return lastPrice;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return new Price(
                    System.currentTimeMillis(),
                    lastPrice.value
            );
        }
    }
}