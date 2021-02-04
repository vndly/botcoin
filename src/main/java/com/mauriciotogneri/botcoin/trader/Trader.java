package com.mauriciotogneri.botcoin.trader;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;

import java.util.List;

public interface Trader
{
    List<NewOrderResponse> process(List<NewOrder> orders);
}