package com.mauriciotogneri.botcoin.market;

import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.mauriciotogneri.botcoin.wallet.Asset;
import com.mauriciotogneri.botcoin.wallet.Currency;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Symbol
{
    public final String name;
    public final Asset assetA;
    public final Asset assetB;

    public Symbol(Currency currencyA, Currency currencyB, @NotNull ExchangeInfo exchangeInfo)
    {
        this.name = String.format("%s%s", currencyA, currencyB);

        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(name);
        int decimalsA = symbolInfo.getBaseAssetPrecision();
        int decimalsB = symbolInfo.getQuotePrecision();
        SymbolFilter filter = symbolInfo.getSymbolFilter(FilterType.LOT_SIZE);
        int stepSize = stepSize(filter.getStepSize());

        this.assetA = new Asset(currencyA, decimalsA, stepSize);
        this.assetB = new Asset(currencyB, decimalsB, stepSize);
    }

    private int stepSize(String value)
    {
        int result = 0;

        BigDecimal stepSize = new BigDecimal(value);
        BigDecimal current = new BigDecimal(String.valueOf(1 / Math.pow(10, result)));

        while (stepSize.compareTo(current) < 0)
        {
            result++;
            current = new BigDecimal(String.valueOf(1 / Math.pow(10, result)));
        }

        return result;
    }

    @Override
    public String toString()
    {
        return name;
    }
}