package com.mauriciotogneri.botcoin.strategy;

import com.binance.api.client.domain.account.NewOrder;
import com.mauriciotogneri.botcoin.trader.OrderSent;

import java.util.List;

public interface Strategy<T>
{
    List<NewOrder> orders(T data);

    List<Object> update(List<OrderSent> sent);
}
