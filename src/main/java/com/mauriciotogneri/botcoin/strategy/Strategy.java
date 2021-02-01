package com.mauriciotogneri.botcoin.strategy;

public interface Strategy
{
    Operation operation(double price);
}