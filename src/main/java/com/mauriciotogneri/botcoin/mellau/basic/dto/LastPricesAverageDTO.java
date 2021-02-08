package com.mauriciotogneri.botcoin.mellau.basic.dto;


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

        // TODO: if the last price it's at the begining make priceEntries[y]
        for (int y = 0; y < ConfigConst.NUMBER_FOR_LONG_AVERAGE; y++) {
            avgLong = new BigDecimal(priceEntries.get(priceEntries.size() - 1 - y).getClose()).add(avgLong);
            if (y < ConfigConst.NUMBER_FOR_SHORT_AVERAGE) {
                avgShort = new BigDecimal(priceEntries.get(priceEntries.size() - 1 - y).getClose()).add(avgShort);
            }
        }

        this.avgLong = avgLong.divide(new BigDecimal(ConfigConst.NUMBER_FOR_LONG_AVERAGE));
        this.avgLong = avgShort.divide(new BigDecimal(ConfigConst.NUMBER_FOR_SHORT_AVERAGE));
    }
}
