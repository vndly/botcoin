package com.mauriciotogneri.botcoin.wallet;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.AssetBalance;
import com.mauriciotogneri.botcoin.exchange.Binance;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Balances
{
    private static final BinanceApiRestClient client = Binance.apiClient();
    private static Account account;

    public static void update()
    {
        synchronized (Balances.class)
        {
            account = client.getAccount();
        }
    }

    @NotNull
    public static BigDecimal balance(@NotNull Asset asset)
    {
        synchronized (Balances.class)
        {
            if (account == null)
            {
                update();
            }

            AssetBalance assetBalance = account.getAssetBalance(asset.currency.name());

            return new BigDecimal(assetBalance.getFree()).setScale(asset.decimals, RoundingMode.DOWN);

        }
    }
}