package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.google.gson.Gson;
import com.mauriciotogneri.botcoin.exchange.BinanceApi;
import com.mauriciotogneri.botcoin.momo.BasicBuyStrategy;
import com.mauriciotogneri.botcoin.momo.BasicSellStrategy;
import com.mauriciotogneri.botcoin.momo.BasicStrategy;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.FilePriceProvider;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.FakeTrader;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;

import java.util.List;

public class Tester
{
    public static void main(String[] args) throws Exception
    {
        testFile();
    }

    private static void testApi()
    {
        BinanceApiRestClient client = BinanceApi.client();

        //List<Trade> trades = client.getMyTrades("BTCEUR");
        //print(trades);

        //CancelOrderResponse cancelOrderResponse = client.cancelOrder(new CancelOrderRequest("BTCEUR", 307513170L));
        //print(cancelOrderResponse);

        /*NewOrderResponse newOrderResponse = client.newOrder(new NewOrder(
                "BTCEUR",
                OrderSide.SELL,
                OrderType.LIMIT,
                TimeInForce.GTC,
                "0.002000", // min 0.0001 BTC
                "31050.00"
        ));
        print(newOrderResponse);*/

        //Order orderStatus = client.getOrderStatus(new OrderStatusRequest("BTCEUR", 307540435L));
        //print(orderStatus);

        List<Order> openOrders = client.getOpenOrders(new OrderRequest("BTCEUR"));
        print(openOrders);
    }

    private static void print(Object object)
    {
        System.out.println(new Gson().newBuilder().setPrettyPrinting().create().toJson(object));
    }

    private static void testFile() throws Exception
    {
        double minEurToTrade = 10;
        double minBtcToTrade = 0.0001;

        double minPercentageDown = 0.01;
        double percentageBuyMultiplier = 70;

        double minPercentageUp = 0.05;
        double percentageSellMultiplier = 100;
        double sellAllLimit = 0.001f;

        Log log = new Log("output/logs.json");

        DataProvider<Price> dataProvider = new FilePriceProvider("input/prices_BTCEUR_1m.csv");

        Balance balanceEUR = new Balance(Currency.EUR, 5000);
        Balance balanceBTC = new Balance(Currency.BTC, 0);
        BasicBuyStrategy buyStrategy = new BasicBuyStrategy(minPercentageDown, percentageBuyMultiplier, minEurToTrade, minBtcToTrade);
        BasicSellStrategy sellStrategy = new BasicSellStrategy(minPercentageUp, percentageSellMultiplier, sellAllLimit, minEurToTrade, minBtcToTrade);
        Strategy<Price> strategy = new BasicStrategy(balanceEUR, balanceBTC, buyStrategy, sellStrategy);

        Trader trader = new FakeTrader();

        Botcoin<Price> botcoin = new Botcoin<>(dataProvider, strategy, trader, log);
        botcoin.start();
    }
}