package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.google.gson.JsonArray;
import com.mauriciotogneri.botcoin.provider.Data;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Log;

import java.util.List;

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
            List<NewOrderResponse> responses = trader.process(orders);
            JsonArray json = strategy.update(responses);
            log.json(data, json);
        }
    }
}