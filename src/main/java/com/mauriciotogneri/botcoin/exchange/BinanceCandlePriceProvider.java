package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.mauriciotogneri.botcoin.config.ConfigConst;
import com.mauriciotogneri.botcoin.provider.DataProvider;

import java.util.List;

public class BinanceCandlePriceProvider implements DataProvider<List<Candlestick>>
{
    private final String symbol;
    private final int frequency;
    private final BinanceApiRestClient client;

    public BinanceCandlePriceProvider(String symbol, int frequency)
    {
        this.symbol = symbol;
        this.frequency = frequency;
        this.client = Binance.apiClient();
    }

    @Override
    public boolean hasData()
    {
        return true;
    }

    @Override
    public List<Candlestick> data() throws Exception
    {
        Thread.sleep(frequency * 1000L);
        Long now = System.currentTimeMillis();
        Long xMinAgo = System.currentTimeMillis() - (ConfigConst.NUMBER_FOR_LONG_AVERAGE * 60000);
        List<Candlestick> tickerPrice = client.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE, ConfigConst.NUMBER_FOR_LONG_AVERAGE, xMinAgo, now);
        return tickerPrice;
    }
}
