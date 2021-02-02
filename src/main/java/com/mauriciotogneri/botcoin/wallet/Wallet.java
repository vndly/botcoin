package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.operations.BuyOperation;
import com.mauriciotogneri.botcoin.operations.SellOperation;

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

    public BuyOperation buy(double toBuy, double price)
    {
        double toSpend = toBuy * price;

        balanceA.amount -= toSpend;
        balanceB.amount += toBuy;
        spent += toSpend;

        return new BuyOperation(balanceB.of(toBuy),
                                balanceA.of(price),
                                balanceA.of(spent),
                                balanceA,
                                balanceB,
                                balanceA.of(totalBalance(price)));
    }

    public SellOperation sell(double toSell, double price)
    {
        double originalCost = toSell * boughtPrice();
        double toGain = toSell * price;
        double profit = toGain - originalCost;

        balanceA.amount += toGain;
        balanceB.amount -= toSell;
        spent -= originalCost;

        return new SellOperation(balanceB.of(toSell),
                                 balanceA.of(price),
                                 balanceA.of(toGain),
                                 balanceA.of(profit),
                                 balanceA,
                                 balanceB,
                                 balanceA.of(totalBalance(price)));
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