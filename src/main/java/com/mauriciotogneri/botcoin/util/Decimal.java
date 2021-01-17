package com.mauriciotogneri.botcoin.util;

import java.math.BigDecimal;

public class Decimal
{
    public static float currency(float value)
    {
        return round(value, 2);
    }

    public static float crypto(float value)
    {
        return round(value, 8);
    }

    private static float round(float value, int decimals)
    {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(decimals, BigDecimal.ROUND_HALF_UP);

        return bigDecimal.floatValue();
    }
}