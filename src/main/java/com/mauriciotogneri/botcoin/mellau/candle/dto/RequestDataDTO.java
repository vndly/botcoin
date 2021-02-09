package com.mauriciotogneri.botcoin.mellau.candle.dto;


import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.TickerPrice;
import java.util.List;

public class RequestDataDTO {
    public List<Candlestick> candlestickBars;
    public TickerPrice tickerPrice;

    public void setTickerPrice(TickerPrice candlestickBars) {
        this.tickerPrice = candlestickBars;
    }

    public void setCandlestickBars(List<Candlestick> candlestickBars) {
        this.candlestickBars = candlestickBars;
    }
}
