package com.mauriciotogneri.botcoin.wallet;

public class Currency
{
    public final String symbol;
    public final int decimals;
    public final int step; // TODO: should be defined at a symbol level

    public static final Currency EUR = new Currency("EUR", 8, 8);
    public static final Currency USD = new Currency("USD", 8, 8);

    public static final Currency BTC = new Currency("BTC", 8, 6);
    public static final Currency ETH = new Currency("ETH", 8, 5);
    public static final Currency USDT = new Currency("USDT", 8, 8);
    public static final Currency XRP = new Currency("XRP", 8, 8);
    public static final Currency DOT = new Currency("DOT", 8, 8);
    public static final Currency ADA = new Currency("ADA", 8, 8);
    public static final Currency LTC = new Currency("LTC", 8, 8);
    public static final Currency LINK = new Currency("LINK", 8, 8);
    public static final Currency BCH = new Currency("BCH", 8, 8);
    public static final Currency BNB = new Currency("BNB", 8, 8);
    public static final Currency XLM = new Currency("XLM", 8, 8);
    public static final Currency USDC = new Currency("USDC", 8, 8);
    public static final Currency UNI = new Currency("UNI", 8, 8);
    public static final Currency WBTC = new Currency("WBTC", 8, 8);
    public static final Currency DOGE = new Currency("DOGE", 8, 8);
    public static final Currency AAVE = new Currency("AAVE", 8, 8);
    public static final Currency BSV = new Currency("BSV", 8, 8);
    public static final Currency EOS = new Currency("EOS", 8, 8);
    public static final Currency XMR = new Currency("XMR", 8, 8);
    public static final Currency XEM = new Currency("XEM", 8, 8);

    public Currency(String symbol, int decimals, int step)
    {
        this.symbol = symbol;
        this.decimals = decimals;
        this.step = step;
    }
}