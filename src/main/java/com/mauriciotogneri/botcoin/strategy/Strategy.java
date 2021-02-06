package com.mauriciotogneri.botcoin.strategy;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.google.gson.JsonArray;
import com.mauriciotogneri.botcoin.provider.Data;

import java.util.List;
import java.util.Map;

public interface Strategy<T extends Data>
{
    List<NewOrder> orders(T data);

    JsonArray update(Map<NewOrder, NewOrderResponse> orders);
}