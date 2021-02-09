package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent.UserDataUpdateEventType;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Binance
{
    @NotNull
    public static BinanceApiClientFactory factory()
    {
        String apiKey = System.getenv("BINANCE_API");
        String secret = System.getenv("BINANCE_SECRET");

        return BinanceApiClientFactory.newInstance(apiKey, secret);
    }

    public static BinanceApiRestClient apiClient()
    {
        BinanceApiClientFactory factory = factory();

        return factory.newRestClient();
    }

    public static void onOrderTradeUpdateEvent(BinanceApiCallback<OrderTradeUpdateEvent> callback)
    {
        BinanceApiRestClient client = apiClient();
        BinanceApiWebSocketClient webSocketClient = factory().newWebSocketClient();
        String listenKey = client.startUserDataStream();
        client.keepAliveUserDataStream(listenKey);
        webSocketClient.onUserDataUpdateEvent(listenKey, response -> {
            if (response.getEventType() == UserDataUpdateEventType.ORDER_TRADE_UPDATE)
            {
                OrderTradeUpdateEvent orderTradeUpdateEvent = response.getOrderTradeUpdateEvent();
                callback.onResponse(orderTradeUpdateEvent);
            }
        });
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
    public static NewOrder limitSell(String symbol, String quantity, String price) {
        return new NewOrder(symbol, OrderSide.SELL, OrderType.LIMIT, TimeInForce.GTC, quantity, price);
    }

    @NotNull
    public static BigDecimal balance(String asset)
    {
        Account account = apiClient().getAccount();
        AssetBalance assetBalance = account.getAssetBalance(asset);

        return new BigDecimal(assetBalance.getFree());
    }
}
