package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.util.Log;

public class Wallet
{
    public final Balance balanceA;
    public final Balance balanceB;
    private double spent;
    private final Log log;

    public Wallet(Balance balanceA, Balance balanceB, Log log)
    {
        this.balanceA = balanceA;
        this.balanceB = balanceB;
        this.log = log;
    }

    public void buy(double toBuy, double price)
    {
        double toSpend = toBuy * price;

        balanceA.amount -= toSpend;
        spent += toSpend;
        balanceB.amount += toBuy;

        log.buy(balanceB.of(toBuy),
                balanceA.of(price),
                balanceA.of(spent),
                balanceA,
                balanceB,
                balanceA.of(totalBalance(price)));
    }

    public void sell(double toSell, double price)
    {
        double originalCost = toSell * boughtPrice();
        double toGain = toSell * price;
        double profit = toGain - originalCost;

        balanceA.amount += toGain;
        spent -= originalCost;
        balanceB.amount -= toSell;

        log.sell(balanceB.of(toSell),
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