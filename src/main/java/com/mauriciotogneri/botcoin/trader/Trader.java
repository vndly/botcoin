package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.account.NewOrder;

import java.util.List;

public interface Trader
{
    List<OrderSent> process(List<NewOrder> orders);
}