package com.mauriciotogneri.botcoin.config;

public class ConfigConst {
    /**
     * For 1m intervals Best long: 2 & Best short: 1
     * */
    public static final Integer NUMBER_FOR_LONG_AVERAGE = 20;
    public static final Integer NUMBER_FOR_SHORT_AVERAGE = 8;

    public static final Integer NUMBER_OF_CANDLES_TO_LOOK_BACK = 5;

    public static final double MIN_EUR_TO_TRADE = 0.03;
    public static final double MIN_BTC_TO_TRADE = 0.0005;
}
