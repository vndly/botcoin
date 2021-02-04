package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.mauriciotogneri.botcoin.trader.Trader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BinanceTrader implements Trader
{
    private final BinanceApiRestClient client;

    public BinanceTrader()
    {
        this.client = BinanceApi.client();
    }

    @Override
    public List<NewOrderResponse> process(@NotNull List<NewOrder> orders)
    {
        List<NewOrderResponse> responses = new ArrayList<>();

        for (NewOrder order : orders)
        {
            //responses.add(client.newOrder(order));
        }

        return responses;
    }
}