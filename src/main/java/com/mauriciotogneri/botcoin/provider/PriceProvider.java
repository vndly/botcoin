package com.mauriciotogneri.botcoin.provider;

public interface PriceProvider
{
    boolean hasMorePrices();

    double price() throws Exception;
}