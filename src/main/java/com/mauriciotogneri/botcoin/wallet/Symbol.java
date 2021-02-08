package com.mauriciotogneri.botcoin.wallet;

public class Symbol
{
    private final String assetA;
    private final String assetB;
    private final int step;

    public Symbol(String assetA, String assetB, int step)
    {
        this.assetA = assetA;
        this.assetB = assetB;
        this.step = step;
    }
}