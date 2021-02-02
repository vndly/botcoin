package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.operation.BuyOperation;
import com.mauriciotogneri.botcoin.operation.SellOperation;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.provider.PriceProvider;
import com.mauriciotogneri.botcoin.strategy.Action;
import com.mauriciotogneri.botcoin.strategy.Intent;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.TradeInfo;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Wallet;

// https://medium.com/swlh/battle-of-the-bots-how-market-makers-fight-it-out-on-crypto-exchanges-2482eb937107
public class Botcoin
{
    private final Wallet wallet;
    private final PriceProvider priceProvider;
    private final Strategy strategy;
    private final Trader trader;
    private final Log log;

    public Botcoin(Wallet wallet, PriceProvider priceProvider, Strategy strategy, Trader trader, Log log)
    {
        this.wallet = wallet;
        this.priceProvider = priceProvider;
        this.strategy = strategy;
        this.trader = trader;
        this.log = log;
    }

    public Balance start() throws Exception
    {
        double lastPrice = 0;

        while (priceProvider.hasPrice())
        {
            Price price = priceProvider.price();
            Intent intent = strategy.intent(price.value);

            if (intent.action == Action.BUY)
            {
                TradeInfo trade = trader.buy(intent);
                BuyOperation buyOperation = wallet.buy(trade);
                log.buy(price, buyOperation);
            }
            else if (intent.action == Action.SELL)
            {
                TradeInfo trade = trader.sell(intent);
                SellOperation sellOperation = wallet.sell(trade);
                log.sell(price, sellOperation);
            }
            else
            {
                log.price(price);
            }

            lastPrice = price.value;
        }

        return wallet.totalBalance(lastPrice);
    }
}