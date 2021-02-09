package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.event.OrderTradeUpdateEvent;
import com.binance.api.client.domain.event.UserDataUpdateEvent.UserDataUpdateEventType;
import com.mauriciotogneri.botcoin.wallet.Currency;

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
    public static BigDecimal balance(@NotNull Currency currency)
    {
        Account account = apiClient().getAccount();
        AssetBalance assetBalance = account.getAssetBalance(currency.name);

        return new BigDecimal(assetBalance.getFree());
    }
}