package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.util.Log;

public class BtcEurWallet implements Wallet
{
    private float balanceEUR;
    private float balanceBTC;
    private float eurSpent;
    private final Log log;

    public BtcEurWallet(float balanceEUR, float balanceBTC, Log log)
    {
        this.balanceEUR = balanceEUR;
        this.balanceBTC = balanceBTC;
        this.eurSpent = 0;
        this.log = log;
    }

    @Override
    public void buy(float buyAmount, float price)
    {
        float eurToSpend = buyAmount * price;

        balanceEUR = balanceEUR - eurToSpend;
        eurSpent = eurSpent + eurToSpend;
        balanceBTC = balanceBTC + buyAmount;

        printBuy(price, eurToSpend, buyAmount);
        printState();
    }

    @Override
    public void sell(float sellAmount, float price)
    {
        float eurToGain = sellAmount * price;
        float originalCost = sellAmount * boughtPrice();

        balanceEUR = balanceEUR + eurToGain;
        eurSpent = eurSpent - originalCost;
        balanceBTC = balanceBTC - sellAmount;

        printSell(price, eurToGain, sellAmount);
        printState();
    }

    private void printBuy(float price, float amountSpent, float btcToBuy)
    {
        log.log("OPERATION:  BUY");
        log.log("PRICE:      " + String.format("%.2f", price) + " EUR");
        log.log("SPENT:      " + String.format("%.2f", amountSpent) + " EUR");
        log.log("AMOUNT:     " + String.format("%.8f", btcToBuy) + " BTC");
    }

    private void printSell(float price, float amountGained, float btcToSell)
    {
        log.log("OPERATION:  SELL");
        log.log("PRICE:      " + String.format("%.2f", price) + " EUR");
        log.log("GAINED:     " + String.format("%.2f", amountGained) + " EUR");
        log.log("AMOUNT:     " + String.format("%.8f", btcToSell) + " BTC");
    }

    private void printState()
    {
        log.log("");
        log.log("BALANCE:    " + String.format("%.2f", balanceEUR) + " EUR");
        log.log("BALANCE:    " + String.format("%.8f", balanceBTC) + " BTC");
        log.log("SPENT:      " + String.format("%.2f", eurSpent) + " EUR");
        log.log("BOUGHT AT:  " + String.format("%.2f", boughtPrice()) + " EUR");
        log.log("====================================");
    }

    public float boughtPrice()
    {
        return eurSpent / balanceBTC;
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