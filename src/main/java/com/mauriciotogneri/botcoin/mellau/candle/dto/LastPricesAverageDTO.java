package com.mauriciotogneri.botcoin.mellau.candle.dto;


import com.binance.api.client.domain.market.Candlestick;
import com.mauriciotogneri.botcoin.config.ConfigConst;

import java.math.BigDecimal;
import java.util.List;

public class LastPricesAverageDTO {
    public BigDecimal avgLong;

    public BigDecimal avgShort;

    public void getAverages(List<Candlestick> priceEntries) {
        BigDecimal avgLong = new BigDecimal(0);
        BigDecimal avgShort = new BigDecimal(0);

        for (int y = 0; y < ConfigConst.NUMBER_FOR_LONG_AVERAGE; y++) {
            avgLong = new BigDecimal(priceEntries.get(priceEntries.size() - 1 - y).getClose()).add(avgLong);
            if (y < ConfigConst.NUMBER_FOR_SHORT_AVERAGE) {
                avgShort = new BigDecimal(priceEntries.get(priceEntries.size() - 1 - y).getClose()).add(avgShort);
            }
        }

        this.avgLong = avgLong.divide(new BigDecimal(ConfigConst.NUMBER_FOR_LONG_AVERAGE));
        this.avgShort = avgShort.divide(new BigDecimal(ConfigConst.NUMBER_FOR_SHORT_AVERAGE));
    }

    public void getOldAverages(List<Candlestick> priceEntries) {
        BigDecimal avgLong = new BigDecimal(0);
        BigDecimal avgShort = new BigDecimal(0);

        for (int y = 0; y < ConfigConst.NUMBER_FOR_LONG_AVERAGE; y++) {
            if (y == 0) {
                continue;
            }
            avgLong = new BigDecimal(priceEntries.get(priceEntries.size() - 1 - y).getClose()).add(avgLong);
            if (y < ConfigConst.NUMBER_FOR_SHORT_AVERAGE) {
                avgShort = new BigDecimal(priceEntries.get(priceEntries.size() - 1 - y).getClose()).add(avgShort);
            }
        }

        this.avgLong = avgLong.divide(new BigDecimal(ConfigConst.NUMBER_FOR_LONG_AVERAGE));
        this.avgShort = avgShort.divide(new BigDecimal(ConfigConst.NUMBER_FOR_SHORT_AVERAGE));
    }
}
