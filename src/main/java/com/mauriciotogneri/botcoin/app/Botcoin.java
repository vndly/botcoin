package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.operations.BuyOperation;
import com.mauriciotogneri.botcoin.operations.SellOperation;
import com.mauriciotogneri.botcoin.provider.PriceProvider;
import com.mauriciotogneri.botcoin.strategy.Action;
import com.mauriciotogneri.botcoin.strategy.Operation;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Wallet;

// https://medium.com/swlh/battle-of-the-bots-how-market-makers-fight-it-out-on-crypto-exchanges-2482eb937107
public class Botcoin
{
    private final Wallet wallet;
    private final PriceProvider priceProvider;
    private final Strategy strategy;
    private final Log log;

    public Botcoin(Wallet wallet, PriceProvider priceProvider, Strategy strategy, Log log)
    {
        this.wallet = wallet;
        this.priceProvider = priceProvider;
        this.strategy = strategy;
        this.log = log;
    }

    public double start() throws Exception
    {
        double lastPrice = 0;

        while (priceProvider.hasPrice())
        {
            double price = priceProvider.price();
            Operation operation = strategy.operation(price);

            if (operation.action == Action.BUY)
            {
                // TODO
                BuyOperation buyOperation = wallet.buy(operation);
                log.buy(buyOperation);
            }
            else if (operation.action == Action.SELL)
            {
                // TODO
                SellOperation sellOperation = wallet.sell(operation);
                log.sell(sellOperation);
            }

            lastPrice = price;
        }

        return wallet.totalBalance(lastPrice);
    }
}