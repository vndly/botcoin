package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.util.Log;

// https://medium.com/swlh/battle-of-the-bots-how-market-makers-fight-it-out-on-crypto-exchanges-2482eb937107
public class MomoWallet implements Wallet
{
    private float balanceEUR;
    private float balanceBTC;
    private float eurSpent;
    private final Log log;

    public MomoWallet(float balanceEUR, float balanceBTC, Log log)
    {
        this.balanceEUR = balanceEUR;
        this.balanceBTC = balanceBTC;
        this.eurSpent = 0;
        this.log = log;
    }

    @Override
    public void buy(float btcToBuy, float price)
    {
        float eurToSpend = btcToBuy * price;

        balanceEUR = balanceEUR - eurToSpend;
        eurSpent = eurSpent + eurToSpend;
        balanceBTC = balanceBTC + btcToBuy;

        printBuy(price, btcToBuy, eurToSpend);
        printState(price);
    }

    @Override
    public void sell(float btcToSell, float price)
    {
        float eurToGain = btcToSell * price;
        float originalCost = btcToSell * boughtPrice();
        float profit = eurToGain - originalCost;

        balanceEUR = balanceEUR + eurToGain;
        eurSpent = eurSpent - originalCost;
        balanceBTC = balanceBTC - btcToSell;

        printSell(price, btcToSell, eurToGain, profit);
        printState(price);
    }

    private void printBuy(float price, float btcToBuy, float eurSpent)
    {
        log.log("OPERATION:  BUY");
        log.log("PRICE:      " + String.format("%.2f", price) + " EUR");
        log.log("AMOUNT:     " + String.format("%.8f", btcToBuy) + " BTC");
        log.log("SPENT:      " + String.format("%.2f", eurSpent) + " EUR");
    }

    private void printSell(float price, float btcToSell, float eurGained, float profit)
    {
        log.log("OPERATION:  SELL");
        log.log("PRICE:      " + String.format("%.2f", price) + " EUR");
        log.log("AMOUNT:     " + String.format("%.8f", btcToSell) + " BTC");
        log.log("GAINED:     " + String.format("%.2f", eurGained) + " EUR");
        log.log("PROFIT:     " + String.format("%.2f", profit) + " EUR");
    }

    private void printState(float price)
    {
        log.log("");
        log.log("BALANCE:    " + String.format("%.2f", balanceEUR) + " EUR");
        log.log("BALANCE:    " + String.format("%.8f", balanceBTC) + " BTC");
        log.log("SPENT:      " + String.format("%.2f", eurSpent) + " EUR");
        log.log("BOUGHT AT:  " + String.format("%.2f", boughtPrice()) + " EUR");
        log.log("TOTAL:      " + String.format("%.2f", balanceEUR + (balanceBTC * price)) + " EUR");
        log.log("\n====================================\n");
    }

    public float boughtPrice()
    {
        return (balanceBTC > 0) ? (eurSpent / balanceBTC) : 0;
    }

    public float balanceBTC()
    {
        return balanceBTC;
    }

    public float balanceEUR()
    {
        return balanceEUR;
    }
}