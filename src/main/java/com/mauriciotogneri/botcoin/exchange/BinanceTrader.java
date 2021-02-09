package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.mauriciotogneri.botcoin.trader.OrderSent;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.json.Json;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BinanceTrader implements Trader
{
    private final BinanceApiRestClient client;

    public BinanceTrader()
    {
        this.client = Binance.apiClient();
    }

    @Override
    public List<OrderSent> process(@NotNull List<NewOrder> orders)
    {
        List<OrderSent> sent = new ArrayList<>();

        for (NewOrder order : orders)
        {
            System.out.printf("Attempt to execute order: %s%n", Json.toJsonString(order));

            NewOrderResponse response = client.newOrder(order);
            sent.add(new OrderSent(order, response));
        }

        return sent;
    }
}