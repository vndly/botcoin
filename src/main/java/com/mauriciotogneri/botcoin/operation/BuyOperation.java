package com.mauriciotogneri.botcoin.operation;

import com.mauriciotogneri.botcoin.wallet.Balance;

public class BuyOperation
{
    public final Balance amount;
    public final Balance price;
    public final Balance spent;
    public final Balance balanceA;
    public final Balance balanceB;
    public final Balance total;

    public BuyOperation(Balance amount, Balance price, Balance spent, Balance balanceA, Balance balanceB, Balance total)
    {
        this.amount = amount;
        this.price = price;
        this.spent = spent;
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.total = total;
    }
}