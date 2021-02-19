package com.mauriciotogneri.botcoin.provider;

public interface DataProvider<T>
{
    boolean hasData();

    T data();
}