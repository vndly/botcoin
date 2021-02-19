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
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.CancelOrderResponse;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent.UserDataUpdateEventType;
import com.mauriciotogneri.botcoin.wallet.Asset;
import com.mauriciotogneri.botcoin.wallet.Balance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    public static Account account()
    {
        return apiClient().getAccount();
    }

    @NotNull
    public static BigDecimal balance(@NotNull Account account, @NotNull Balance balance)
    {
        return balance(account, balance.asset);
    }

    @NotNull
    public static CancelOrderResponse cancelOrder(String symbol, Long orderId) {
        return apiClient().cancelOrder(new CancelOrderRequest(symbol, orderId));
    }

    @NotNull
    public static BigDecimal balance(@NotNull Account account, @NotNull Asset asset)
    {
        AssetBalance assetBalance = account.getAssetBalance(asset.currency.name());

        return new BigDecimal(assetBalance.getFree()).setScale(asset.decimals, RoundingMode.DOWN);
    }
}
