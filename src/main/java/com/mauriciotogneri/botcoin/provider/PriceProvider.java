package com.mauriciotogneri.botcoin.provider;

public interface PriceProvider
{
    float price() throws Exception;
}