package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;

public class OrderSent
{
    public final NewOrder order;
    public final NewOrderResponse response;

    public OrderSent(NewOrder order, NewOrderResponse response)
    {
        this.order = order;
        this.response = response;
    }
}