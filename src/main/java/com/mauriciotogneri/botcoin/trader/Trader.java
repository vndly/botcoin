package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;

import java.util.List;
import java.util.Map;

public interface Trader
{
    Map<NewOrder, NewOrderResponse> process(List<NewOrder> orders);
}