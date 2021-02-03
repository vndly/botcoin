package com.mauriciotogneri.botcoin.provider;

// TODO: https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md
public class BinancePriceWebSocketProvider implements DataProvider<Price>
{
    public BinancePriceWebSocketProvider(String symbol)
    {
    }

    @Override
    public boolean hasData()
    {
        return true;
    }

    @Override
    public Price data() throws Exception
    {
        return new Price(System.currentTimeMillis(), 0);
    }
}