package com.mauriciotogneri.botcoin.provider;

// TODO: https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md
public class BinanceWebSocketProvider implements PriceProvider
{
    public BinanceWebSocketProvider(String symbol)
    {
    }

    @Override
    public boolean hasPrice()
    {
        return true;
    }

    @Override
    public double price() throws Exception
    {
        return 0;
    }
}