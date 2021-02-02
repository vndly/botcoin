package com.mauriciotogneri.botcoin.trader;

import com.mauriciotogneri.botcoin.strategy.Intent;

public interface Trader
{
    TradeInfo buy(Intent intent);

    TradeInfo sell(Intent intent);
}