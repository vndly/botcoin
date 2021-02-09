package com.mauriciotogneri.botcoin.market;

import com.binance.api.client.domain.general.ExchangeInfo;
import com.binance.api.client.domain.general.FilterType;
import com.binance.api.client.domain.general.SymbolFilter;
import com.binance.api.client.domain.general.SymbolInfo;
import com.mauriciotogneri.botcoin.exchange.Binance;
import com.mauriciotogneri.botcoin.wallet.Currency;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Symbol
{
    public final Currency currencyA;
    public final Currency currencyB;

    public Symbol(Currency currencyA, Currency currencyB)
    {
        String name = name(currencyA, currencyB);

        ExchangeInfo exchangeInfo = Binance.apiClient().getExchangeInfo();
        SymbolInfo symbolInfo = exchangeInfo.getSymbolInfo(name);
        int decimalsA = symbolInfo.getBaseAssetPrecision();
        int decimalsB = symbolInfo.getQuotePrecision();
        SymbolFilter filter = symbolInfo.getSymbolFilter(FilterType.LOT_SIZE);
        BigDecimal minQuantity = new BigDecimal(filter.getMinQty());
        int stepSize = stepSize(filter.getStepSize());

        this.currencyA = currencyA.with(decimalsA, stepSize);
        this.currencyB = currencyB.with(decimalsB, stepSize);
    }

    private int stepSize(String value)
    {
        BigDecimal stepSize = new BigDecimal(value);

        return 0; // TODO
    }

    private String name(@NotNull Currency currencyA, @NotNull Currency currencyB)
    {
        return String.format("%s%s", currencyA.name, currencyB.name);
    }

    @Override
    public String toString()
    {
        return name(currencyA, currencyB);
    }
}