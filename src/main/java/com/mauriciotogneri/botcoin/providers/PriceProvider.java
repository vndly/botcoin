package com.mauriciotogneri.botcoin.providers;

public interface PriceProvider
{
    float price() throws Exception;
}