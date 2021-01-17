package com.mauriciotogneri.botcoin.wallet;

import static com.mauriciotogneri.botcoin.util.Decimal.crypto;
import static com.mauriciotogneri.botcoin.util.Decimal.currency;

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
        float usdToSpend = currency(buyAmount * price);

        this.balanceEUR = currency(this.balanceEUR - usdToSpend);
        //this.usdSpent = currency(this.usdSpent + usdToSpend);
        this.balanceBTC = crypto(this.balanceBTC + buyAmount);
        //this.boughtPrice = currency(this.usdSpent / this.balanceBTC);

        //printBuy(price, usdToSpend, buyAmount);
        //printState(this);
    }

    @Override
    public void sell(float sellAmount, float price)
    {
    }

    @Override
    public float balanceSource()
    {
        return balanceBTC;
    }

    @Override
    public float balanceTarget()
    {
        return balanceEUR;
    }
}