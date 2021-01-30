package com.mauriciotogneri.botcoin.wallet;

public interface Wallet
{
    void buy(double buyAmount, double price);

    void sell(double sellAmount, double price);

    double totalBalance(double price);
}