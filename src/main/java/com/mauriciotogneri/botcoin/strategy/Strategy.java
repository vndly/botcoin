package com.mauriciotogneri.botcoin.strategy;

import com.mauriciotogneri.botcoin.provider.Data;

public interface Strategy<T extends Data>
{
    Intent intent(T data);
}