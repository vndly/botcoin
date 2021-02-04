package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.mauriciotogneri.botcoin.trader.Trader;

import java.util.ArrayList;
import java.util.List;

public class BinanceTrader implements Trader
{
    @Override
    public List<NewOrderResponse> process(List<NewOrder> orders)
    {
        // TODO: do the trading
        return new ArrayList<>();
    }
}