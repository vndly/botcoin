package com.mauriciotogneri.botcoin.provider;

public interface PriceProvider
{
    boolean hasMorePrices();

    float price() throws Exception;
}