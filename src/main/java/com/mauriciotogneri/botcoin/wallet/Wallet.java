package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.operations.BuyOperation;
import com.mauriciotogneri.botcoin.operations.SellOperation;
import com.mauriciotogneri.botcoin.strategy.Intent;

public class Wallet
{
    public final Balance balanceA;
    public final Balance balanceB;
    public double spent;

    public Wallet(Balance balanceA, Balance balanceB)
    {
        this.balanceA = balanceA;
        this.balanceB = balanceB;
    }

    public BuyOperation buy(Intent intent)
    {
        double toSpend = intent.amount * intent.price;

        balanceA.amount -= toSpend;
        balanceB.amount += intent.amount;
        spent += toSpend;

        return new BuyOperation(balanceB.of(intent.amount),
                                balanceA.of(intent.price),
                                balanceA.of(spent),
                                balanceA,
                                balanceB,
                                balanceA.of(totalBalance(intent.price)));
    }

    public SellOperation sell(Intent intent)
    {
        double originalCost = intent.amount * boughtPrice();
        double toGain = intent.amount * intent.price;
        double profit = toGain - originalCost;

        balanceA.amount += toGain;
        balanceB.amount -= intent.amount;
        spent -= originalCost;

        return new SellOperation(balanceB.of(intent.amount),
                                 balanceA.of(intent.price),
                                 balanceA.of(toGain),
                                 balanceA.of(profit),
                                 balanceA,
                                 balanceB,
                                 balanceA.of(totalBalance(intent.price)));
    }

    public double boughtPrice()
    {
        return (balanceB.amount > 0) ? (spent / balanceB.amount) : 0;
    }

    public double totalBalance(double price)
    {
        return balanceA.amount + (balanceB.amount * price);
    }
}