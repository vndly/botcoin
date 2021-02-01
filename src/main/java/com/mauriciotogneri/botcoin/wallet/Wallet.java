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

        printBuy(price, toBuy, toSpend);
        printState(price);
    }

    public void sell(double toSell, double price)
    {
        double originalCost = toSell * boughtPrice();
        double toGain = toSell * price;

        balanceA.amount += toGain;
        spent -= originalCost;
        balanceB.amount -= toSell;

        printSell(price, toSell, toGain);
        printState(price);
    }

    private void printBuy(double price, double toBuy, double spent)
    {
        log.log("OPERATION:  BUY");
        log.log("PRICE:      " + balanceA.format(price));
        log.log("AMOUNT:     " + balanceB.format(toBuy));
        log.log("SPENT:      " + balanceA.format(spent));
    }

    private void printSell(double price, double toSell, double gained)
    {
        log.log("OPERATION:  SELL");
        log.log("PRICE:      " + balanceA.format(price));
        log.log("AMOUNT:     " + balanceB.format(toSell));
        log.log("GAINED:     " + balanceA.format(gained));
    }

    private void printState(double price)
    {
        log.log("");
        log.log("BALANCE:    " + balanceA.toString());
        log.log("BALANCE:    " + balanceB.toString());
        log.log("TOTAL:      " + balanceA.format(totalBalance(price)) + " " + balanceA.currency);
        log.log("\n====================================\n");
    }

    public double boughtPrice()
    {
        return (balanceB.amount > 0) ? (spent / balanceB.amount) : 0;
    }

    public double totalBalance(double price)
    {
        return balanceA.amount + (balanceB.amount * price);
    }

    public Balance balanceA()
    {
        return balanceB;
    }

    public Balance balanceB()
    {
        return balanceA;
    }
}