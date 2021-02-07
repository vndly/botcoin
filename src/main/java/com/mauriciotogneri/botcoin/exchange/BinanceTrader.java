package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Json;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BinanceTrader implements Trader
{
    private final BinanceApiRestClient client;

    public BinanceTrader()
    {
        this.client = Binance.apiClient();
    }

    @Override
    public Map<NewOrder, NewOrderResponse> process(@NotNull List<NewOrder> orders)
    {
        Map<NewOrder, NewOrderResponse> responses = new HashMap<>();

        for (NewOrder order : orders)
        {
            System.out.printf("Attempt to execute order: %s%n", Json.toJsonString(order));

            NewOrderResponse response = client.newOrder(order);
            responses.put(order, response);
        }

        return responses;
    }
}