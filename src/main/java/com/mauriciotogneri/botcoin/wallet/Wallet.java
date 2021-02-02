package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.operations.BuyOperation;
import com.mauriciotogneri.botcoin.operations.SellOperation;
import com.mauriciotogneri.botcoin.strategy.Operation;

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

    public BuyOperation buy(Operation operation)
    {
        double toSpend = operation.amount * operation.price;

        balanceA.amount -= toSpend;
        balanceB.amount += operation.amount;
        spent += toSpend;

        return new BuyOperation(balanceB.of(operation.amount),
                                balanceA.of(operation.price),
                                balanceA.of(spent),
                                balanceA,
                                balanceB,
                                balanceA.of(totalBalance(operation.price)));
    }

    public SellOperation sell(Operation operation)
    {
        double originalCost = operation.amount * boughtPrice();
        double toGain = operation.amount * operation.price;
        double profit = toGain - originalCost;

        balanceA.amount += toGain;
        balanceB.amount -= operation.amount;
        spent -= originalCost;

        return new SellOperation(balanceB.of(operation.amount),
                                 balanceA.of(operation.price),
                                 balanceA.of(toGain),
                                 balanceA.of(profit),
                                 balanceA,
                                 balanceB,
                                 balanceA.of(totalBalance(operation.price)));
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