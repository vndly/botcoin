package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Trade;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.log.Log;

import java.util.List;

public class BinanceAPI
{
    public static void main(String[] args)
    {
        BinanceApiRestClient client = Binance.apiClient();

        //Account account = Binance.account();
        //Log.jsonConsole(account);

        List<Trade> trades = client.getMyTrades("ETHBTC");
        Log.jsonConsole(trades);

        //CancelOrderResponse cancelOrderResponse = client.cancelOrder(new CancelOrderRequest("BTCEUR", 307513170L));
        //Log.jsonConsole(cancelOrderResponse);

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
        //Log.jsonConsole(newOrderResponse);

        //Order orderStatus = client.getOrderStatus(new OrderStatusRequest("BTCEUR", 307540435L));
        //Log.jsonConsole(orderStatus);

        //List<Order> openOrders = client.getOpenOrders(new OrderRequest("BTCEUR"));
        //Log.jsonConsole(openOrders);
    }
}