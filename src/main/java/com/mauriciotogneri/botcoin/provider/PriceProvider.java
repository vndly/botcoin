package com.mauriciotogneri.botcoin.provider;

public interface PriceProvider
{
    boolean hasPrice();

    Price price() throws Exception;
}