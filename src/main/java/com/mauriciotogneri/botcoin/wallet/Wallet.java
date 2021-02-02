package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.operation.BuyOperation;
import com.mauriciotogneri.botcoin.operation.SellOperation;
import com.mauriciotogneri.botcoin.trader.TradeInfo;

import org.jetbrains.annotations.NotNull;

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

    public BuyOperation buy(@NotNull TradeInfo trade)
    {
        double toSpend = trade.quantity * trade.price;

        balanceA.amount -= toSpend;
        balanceB.amount += trade.quantity;
        spent += toSpend;

        return new BuyOperation(balanceB.of(trade.quantity),
                                balanceA.of(trade.price),
                                balanceA.of(spent),
                                balanceA,
                                balanceB,
                                totalBalance(trade.price));
    }

    public SellOperation sell(@NotNull TradeInfo trade)
    {
        double originalCost = trade.quantity * boughtPrice();
        double toGain = trade.quantity * trade.price;
        double profit = toGain - originalCost;

        balanceA.amount += toGain;
        balanceB.amount -= trade.quantity;
        spent -= originalCost;

        return new SellOperation(balanceB.of(trade.quantity),
                                 balanceA.of(trade.price),
                                 balanceA.of(toGain),
                                 balanceA.of(profit),
                                 balanceA,
                                 balanceB,
                                 totalBalance(trade.price));
    }

    public double boughtPrice()
    {
        return (balanceB.amount > 0) ? (spent / balanceB.amount) : 0;
    }

    public Balance totalBalance(double price)
    {
        return balanceA.of(balanceA.amount + (balanceB.amount * price));
    }
}