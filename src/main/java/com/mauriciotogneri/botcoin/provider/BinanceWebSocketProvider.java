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
    public Price price() throws Exception
    {
        return new Price(
                System.currentTimeMillis(),
                0
        );
    }
}