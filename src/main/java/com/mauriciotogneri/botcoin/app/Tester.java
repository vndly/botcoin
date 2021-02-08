package com.mauriciotogneri.botcoin.app;

import com.binance.api.client.BinanceApiRestClient;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.exchange.BinancePriceProvider;
import com.mauriciotogneri.botcoin.exchange.BinanceTrader;
import com.mauriciotogneri.botcoin.momo.basic.BasicStrategy;
import com.mauriciotogneri.botcoin.provider.DataProvider;
import com.mauriciotogneri.botcoin.provider.Price;
import com.mauriciotogneri.botcoin.strategy.Strategy;
import com.mauriciotogneri.botcoin.trader.Trader;
import com.mauriciotogneri.botcoin.util.Json;
import com.mauriciotogneri.botcoin.util.Log;
import com.mauriciotogneri.botcoin.wallet.Balance;
import com.mauriciotogneri.botcoin.wallet.Currency;

public class Tester
{
    public static void main(String[] args) throws Exception
    {
        //testFile();
        testApi();
    }

    private static void testApi()
    {
        BinanceApiRestClient client = Binance.apiClient();

        //Account account = client.getAccount();

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

    // 92.73587834 EUR
    // 93.61401233 EUR
    // 0.00000016 BTC
    private static void testFile() throws Exception
    {
        String minEurToTrade = "10";
        String minBtcToTrade = "0.0005";

        String minPercentageDown = "0.01";
        String percentageBuyMultiplier = "50";

        String minPercentageUp = "0.02";
        String percentageSellMultiplier = "100";
        String sellAllLimit = "0.001";

        //DataProvider<Price> dataProvider = new FilePriceProvider("input/prices_BTCEUR_1m.csv");
        DataProvider<Price> dataProvider = new BinancePriceProvider("BTCEUR", 10);

        Balance balanceEUR = new Balance(Currency.EUR, "50");
        Balance balanceBTC = new Balance(Currency.BTC, "0");
        Strategy<Price> strategy = new BasicStrategy(balanceEUR,
                                                     balanceBTC,
                                                     minPercentageDown,
                                                     percentageBuyMultiplier,
                                                     minPercentageUp,
                                                     percentageSellMultiplier,
                                                     sellAllLimit,
                                                     minEurToTrade,
                                                     minBtcToTrade);

        //Trader trader = new FakeTrader();
        Trader trader = new BinanceTrader();

        Log log = new Log("output/logs.json");

        Botcoin<Price> botcoin = new Botcoin<>(dataProvider, strategy, trader, log);
        botcoin.start();
    }
}