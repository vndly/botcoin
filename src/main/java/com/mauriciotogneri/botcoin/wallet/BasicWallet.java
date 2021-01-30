package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.util.Log;

// https://medium.com/swlh/battle-of-the-bots-how-market-makers-fight-it-out-on-crypto-exchanges-2482eb937107
public class BasicWallet implements Wallet
{
    private double balanceEUR;
    private double balanceBTC;
    private double eurSpent;
    private final Log log;

    public BasicWallet(double balanceEUR, double balanceBTC, Log log)
    {
        this.balanceEUR = balanceEUR;
        this.balanceBTC = balanceBTC;
        this.eurSpent = 0;
        this.log = log;
    }

    @Override
    public void buy(double btcToBuy, double price)
    {
        boolean firstBuy = balanceBTC == 0;
        double eurToSpend = btcToBuy * price;

        balanceEUR = balanceEUR - eurToSpend;
        eurSpent = eurSpent + eurToSpend;
        balanceBTC = balanceBTC + btcToBuy;

        printBuy(price, btcToBuy, eurToSpend, firstBuy);
        printState(price);
    }

    @Override
    public void sell(double btcToSell, double price)
    {
        boolean sellAll = btcToSell == balanceBTC;
        double eurToGain = btcToSell * price;
        double originalCost = btcToSell * boughtPrice();
        double profit = eurToGain - originalCost;

        balanceEUR = balanceEUR + eurToGain;
        eurSpent = eurSpent - originalCost;
        balanceBTC = balanceBTC - btcToSell;

        printSell(price, btcToSell, eurToGain, profit, sellAll);
        printState(price);
    }

    @Override
    public double totalBalance(double price)
    {
        return balanceEUR + (balanceBTC * price);
    }

    private void printBuy(double price, double btcToBuy, double eurSpent, boolean firstBuy)
    {
        log.log("OPERATION:  " + (firstBuy ? "FIRST BUY" : "BUY DOWN"));
        log.log("PRICE:      " + String.format("%.2f", price) + " EUR");
        log.log("AMOUNT:     " + String.format("%.8f", btcToBuy) + " BTC");
        log.log("SPENT:      " + String.format("%.2f", eurSpent) + " EUR");
    }

    private void printSell(double price, double btcToSell, double eurGained, double profit, boolean sellAll)
    {
        log.log("OPERATION:  " + (sellAll ? "SELL ALL" : "SELL PARTIAL"));
        log.log("PRICE:      " + String.format("%.2f", price) + " EUR");
        log.log("AMOUNT:     " + String.format("%.8f", btcToSell) + " BTC");
        log.log("GAINED:     " + String.format("%.2f", eurGained) + " EUR");
        log.log("PROFIT:     " + String.format("%.2f", profit) + " EUR");
    }

    private void printState(double price)
    {
        log.log("");
        log.log("BALANCE:    " + String.format("%.2f", balanceEUR) + " EUR");
        log.log("BALANCE:    " + String.format("%.8f", balanceBTC) + " BTC");
        log.log("SPENT:      " + String.format("%.2f", eurSpent) + " EUR");
        log.log("BOUGHT AT:  " + String.format("%.2f", boughtPrice()) + " EUR");
        log.log("TOTAL:      " + String.format("%.2f", totalBalance(price)) + " EUR");
        log.log("\n====================================\n");
    }

    public double boughtPrice()
    {
        return (balanceBTC > 0) ? (eurSpent / balanceBTC) : 0;
    }

    public double balanceBTC()
    {
        return balanceBTC;
    }

    public double balanceEUR()
    {
        return balanceEUR;
    }
}