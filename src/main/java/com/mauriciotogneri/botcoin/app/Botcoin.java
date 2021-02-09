package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.account.NewOrder;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.OrderSent;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.log.LogEntry;

import java.util.List;

public class Botcoin<T>
{
    private final DataProvider<T> dataProvider;
    private final Strategy<T> strategy;
    private final Trader trader;
    private final Log log;

    public Botcoin(DataProvider<T> dataProvider, Strategy<T> strategy, Trader trader, Log log)
    {
        this.dataProvider = dataProvider;
        this.strategy = strategy;
        this.trader = trader;
        this.log = log;
    }

    public void start()
    {
        while (dataProvider.hasData())
        {
            T data = dataProvider.data();
            List<NewOrder> orders = strategy.orders(data);
            List<OrderSent> sent = trader.process(orders);
            List<Object> events = strategy.update(sent);

            LogEntry logEntry = new LogEntry(data, sent, events);
            log.jsonFile(logEntry);

            if (logEntry.hasEvents())
            {
                log.jsonConsole(logEntry);
            }
        }
    }
}