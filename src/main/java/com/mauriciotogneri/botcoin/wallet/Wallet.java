package com.mauriciotogneri.botcoin.wallet;

public interface Wallet
{
    void buy(float buyAmount, float price);

    void sell(float sellAmount, float price);
}