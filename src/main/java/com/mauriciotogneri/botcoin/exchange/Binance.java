package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Binance
{
    public static BinanceApiRestClient apiClient()
    {
        String apiKey = System.getenv("BINANCE_API");
        String secret = System.getenv("BINANCE_SECRET");

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secret);

        return factory.newRestClient();
    }

    @NotNull
    public static NewOrder buyMarketOrder(String symbol, @NotNull BigDecimal quantity)
    {
        return marketOrder(OrderSide.BUY, symbol, quantity);
    }

    @NotNull
    public static NewOrder sellMarketOrder(String symbol, @NotNull BigDecimal quantity)
    {
        return marketOrder(OrderSide.SELL, symbol, quantity);
    }

    @NotNull
    public static NewOrder marketOrder(OrderSide orderSide, String symbol, @NotNull BigDecimal quantity)
    {
        return new NewOrder(
                symbol,
                orderSide,
                OrderType.MARKET,
                null,
                quantity.toString()
        );
    }

    @NotNull
    public static BigDecimal balance(String asset)
    {
        Account account = apiClient().getAccount();
        AssetBalance assetBalance = account.getAssetBalance(asset);

        return new BigDecimal(assetBalance.getFree());
    }
}