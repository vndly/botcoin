package com.mauriciotogneri.botcoin.provider;

public interface PriceProvider
{
    boolean hasPrice();

    double price() throws Exception;
}