package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.Price;

import java.math.BigDecimal;

public class BinancePriceProvider implements DataProvider<Price>
{
    private final String symbol;
    private final int frequency;
    private final BinanceApiRestClient client;

    public BinancePriceProvider(String symbol, int frequency)
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
    public Price data() throws Exception
    {
        Thread.sleep(frequency * 1000L);
        TickerPrice tickerPrice = client.getPrice(symbol);

        return new Price(
                System.currentTimeMillis(),
                new BigDecimal(tickerPrice.getPrice())
        );
    }
}