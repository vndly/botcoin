package com.mauriciotogneri.botcoin;

public class Binance
{
    private final HttpRequest httpRequest = new HttpRequest();

    public float price(String symbol)
    {
        String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + symbol;
        Payload payload = httpRequest.execute(url, Payload.class);

        return Decimal.currency(payload.price);
    }

    public static class Payload
    {
        public float price;
    }
}