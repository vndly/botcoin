package com.mauriciotogneri.botcoin.strategy;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;

import java.util.List;
import java.util.Map;

public interface Strategy<T>
{
    List<NewOrder> orders(T data);

    List<Object> update(Map<NewOrder, NewOrderResponse> orders);
}
