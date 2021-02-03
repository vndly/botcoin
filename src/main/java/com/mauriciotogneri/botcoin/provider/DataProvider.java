package com.mauriciotogneri.botcoin.provider;

public interface DataProvider<T extends Data>
{
    boolean hasData();

    T data() throws Exception;
}