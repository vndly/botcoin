package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.json.Json;

public class BinanceAPI
{
    public static void main(String[] args)
    {
        BinanceApiRestClient client = Binance.apiClient();

        Account account = client.getAccount();
        print(account);

        //List<Trade> trades = client.getMyTrades("BTCEUR");
        //print(trades);

        //CancelOrderResponse cancelOrderResponse = client.cancelOrder(new CancelOrderRequest("BTCEUR", 307513170L));
        //print(cancelOrderResponse);

        /*NewOrder order = new NewOrder(
                "BTCEUR",
                OrderSide.BUY,
                OrderType.MARKET,
                null,
                "0.0001", // min 0.0005 BTC
                "31050.00"
        );*/
        //NewOrder order = Binance.sellMarketOrder("BTCEUR", new BigDecimal("0.0005"));
        //NewOrderResponse newOrderResponse = client.newOrder(order);
        //print(newOrderResponse);

        //Order orderStatus = client.getOrderStatus(new OrderStatusRequest("BTCEUR", 307540435L));
        //print(orderStatus);

        //List<Order> openOrders = client.getOpenOrders(new OrderRequest("BTCEUR"));
        //print(openOrders);
    }

    private static void print(Object object)
    {
        System.out.println(Json.toJsonString(object));
    }
}