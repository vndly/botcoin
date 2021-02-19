package com.mauriciotogneri.botcoin.exchange;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.TickerPrice;
import com.mauriciotogneri.botcoin.config.ConfigConst;
import com.mauriciotogneri.botcoin.mellau.candle.dto.RequestDataDTO;
import com.mauriciotogneri.botcoin.provider.DataProvider;

import java.util.List;

public class BinanceMellauPriceProvider implements DataProvider<RequestDataDTO>
{
    private final String symbol;
    private final BinanceApiRestClient client;
    private final DataProviderSleepTime dataProviderSleepTime;

    public BinanceMellauPriceProvider(String symbol, DataProviderSleepTime dataProviderSleepTime)
    {
        this.dataProviderSleepTime = dataProviderSleepTime;
        this.symbol = symbol;
        this.client = Binance.apiClient();
    }

    @Override
    public boolean hasData()
    {
        return true;
    }

    @Override
    public RequestDataDTO data() {
        try {
            Thread.sleep(dataProviderSleepTime.value);
        } catch (Exception e) {
            // SAD
        }

        Long now = System.currentTimeMillis();
        Long xMinAgo = now - ((ConfigConst.NUMBER_FOR_LONG_AVERAGE + 5) * 60000);
        List<Candlestick> candlestickBars = client.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE, ConfigConst.NUMBER_FOR_LONG_AVERAGE, xMinAgo, now);
        TickerPrice tickerPrice = client.getPrice(symbol);

        RequestDataDTO requestedData = new RequestDataDTO();
        requestedData.setTickerPrice(tickerPrice);
        requestedData.setCandlestickBars(candlestickBars);

        return requestedData;
    }
}
