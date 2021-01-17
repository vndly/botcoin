package com.mauriciotogneri.botcoin.wallet;

public class BtcEurWallet implements Wallet
{
    private float balanceBTC;
    private float balanceEUR;

    public BtcEurWallet(float balanceBTC, float balanceEUR)
    {
        this.balanceBTC = balanceBTC;
        this.balanceEUR = balanceEUR;
    }

    @Override
    public void buy(float buyAmount, float price)
    {
    }

    @Override
    public void sell(float sellAmount, float price)
    {
    }
}