package com.mauriciotogneri.botcoin.wallet;

import com.mauriciotogneri.botcoin.util.Log;

import static com.mauriciotogneri.botcoin.util.Decimal.crypto;
import static com.mauriciotogneri.botcoin.util.Decimal.currency;

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
        float eurToSpend = currency(buyAmount * price);

        balanceEUR = currency(balanceEUR - eurToSpend);
        eurSpent = currency(eurSpent + eurToSpend);
        balanceBTC = crypto(balanceBTC + buyAmount);

        printBuy(price, eurToSpend, buyAmount);
        printState();
    }

    @Override
    public void sell(float sellAmount, float price)
    {
        float eurToGain = currency(sellAmount * price);
        float originalCost = currency(sellAmount * boughtPrice());

        balanceEUR = currency(balanceEUR + eurToGain);
        eurSpent = currency(eurSpent - originalCost);
        balanceBTC = crypto(balanceBTC - sellAmount);

        printSell(price, eurToGain, sellAmount);
        printState();
    }

    private void printBuy(float price, float amountSpent, float btcToBuy)
    {
        log.log("OPERATION:  BUY");
        log.log("PRICE:      " + price + " EUR");
        log.log("SPENT:      " + amountSpent + " EUR");
        log.log("AMOUNT:     " + String.format("%.8f", btcToBuy) + " BTC");
    }

    private void printSell(float price, float amountGained, float btcToSell)
    {
        log.log("OPERATION:  SELL");
        log.log("PRICE:      " + price + " EUR");
        log.log("GAINED:     " + amountGained + " EUR");
        log.log("AMOUNT:     " + String.format("%.8f", btcToSell) + " BTC");
    }

    private void printState()
    {
        log.log("");
        log.log("BALANCE:    " + balanceEUR + " EUR");
        log.log("BALANCE:    " + String.format("%.8f", balanceBTC) + " BTC");
        log.log("SPENT:      " + eurSpent + " EUR");
        log.log("BOUGHT AT:  " + boughtPrice() + " EUR");
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