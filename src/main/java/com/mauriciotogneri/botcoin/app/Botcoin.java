package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.operation.BuyOperation;
import com.mauriciotogneri.botcoin.operation.SellOperation;
import com.mauriciotogneri.botcoin.provider.Data;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.strategy.Action;
import com.mauriciotogneri.botcoin.strategy.Intent;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.TradeInfo;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Wallet;

public class Botcoin<T extends Data>
{
    private final Wallet wallet;
    private final DataProvider<T> dataProvider;
    private final Strategy<T> strategy;
    private final Trader trader;
    private final Log log;

    public Botcoin(Wallet wallet, DataProvider<T> dataProvider, Strategy<T> strategy, Trader trader, Log log)
    {
        this.wallet = wallet;
        this.dataProvider = dataProvider;
        this.strategy = strategy;
        this.trader = trader;
        this.log = log;
    }

    public void start() throws Exception
    {
        while (dataProvider.hasData())
        {
            T data = dataProvider.data();
            Intent intent = strategy.intent(data);

            if (intent.action == Action.BUY)
            {
                TradeInfo trade = trader.buy(intent);
                BuyOperation buyOperation = wallet.buy(trade);
                log.buy(data, buyOperation);
            }
            else if (intent.action == Action.SELL)
            {
                TradeInfo trade = trader.sell(intent);
                SellOperation sellOperation = wallet.sell(trade);
                log.sell(data, sellOperation);
            }
            else
            {
                log.price(data);
            }
        }
    }
}