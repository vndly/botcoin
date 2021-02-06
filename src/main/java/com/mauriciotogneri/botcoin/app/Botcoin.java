package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.mauriciotogneri.botcoin.provider.Data;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.util.LogEntry;

import java.util.List;
import java.util.Map;

public class Botcoin<T extends Data>
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

    public void start() throws Exception
    {
        while (dataProvider.hasData())
        {
            T data = dataProvider.data();
            List<NewOrder> orders = strategy.orders(data);
            Map<NewOrder, NewOrderResponse> responses = trader.process(orders);
            List<Object> events = strategy.update(responses);
            LogEntry logEntry = new LogEntry(data, events);
            log.json(logEntry);
        }
    }
}