package com.mauriciotogneri.botcoin.operations;

import com.mauriciotogneri.botcoin.wallet.Balance;

public class SellOperation
{
    public final Balance amount;
    public final Balance price;
    public final Balance gained;
    public final Balance profit;
    public final Balance balanceA;
    public final Balance balanceB;
    public final Balance total;

    public SellOperation(Balance amount, Balance price, Balance gained, Balance profit, Balance balanceA, Balance balanceB, Balance total)
    {
        this.amount = amount;
        this.price = price;
        this.gained = gained;
        this.profit = profit;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.total = total;
    }
}