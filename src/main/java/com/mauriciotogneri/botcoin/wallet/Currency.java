package com.mauriciotogneri.botcoin.wallet;

public enum Currency
{
    EUR,
    USD,
    BTC,
    ETH,
    USDT,
    XRP,
    DOT,
    ADA,
    LTC,
    LINK,
    BCH,
    BNB,
    XLM,
    USDC,
    UNI,
    WBTC,
    DOGE,
    AAVE,
    BSV,
    EOS,
    XMR,
    XEM,
    VET,
    GRT,
    ZIL,
    TRX;

    public static Currency[] currencies(String symbol)
    {
        try
        {
            String with4 = symbol.substring(0, 4);
            Currency currency = Currency.valueOf(with4);

            Currency[] result = new Currency[2];
            result[0] = currency;
            result[1] = Currency.valueOf(symbol.replace(with4, ""));

            return result;
        }
        catch (Exception e)
        {
            String with3 = symbol.substring(0, 3);
            Currency currency = Currency.valueOf(with3);

            Currency[] result = new Currency[2];
            result[0] = currency;
            result[1] = Currency.valueOf(symbol.replace(with3, ""));

            return result;
        }
    }
}