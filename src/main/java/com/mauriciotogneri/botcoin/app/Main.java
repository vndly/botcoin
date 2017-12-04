package com.mauriciotogneri.botcoin.app;

import com.mauriciotogneri.botcoin.log.Log;
import com.mauriciotogneri.botcoin.providers.BitStamp;
import com.mauriciotogneri.botcoin.providers.CoinDesk;
import com.mauriciotogneri.botcoin.providers.Provider;
import com.mauriciotogneri.botcoin.providers.eToro;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main
{
    private final Log log;

    public Main() throws IOException
    {
        this.log = new Log(new File(String.format("logs/log_%s.csv", System.currentTimeMillis())));
    }

    public void start() throws Exception
    {
        List<Provider> providers = new ArrayList<>();
        providers.add(new eToro());
        providers.add(new CoinDesk());
        //providers.add(new Blockchain());
        //providers.add(new Bitaps());
        providers.add(new BitStamp());
        //providers.add(new CexIO());

        List<String> names = new ArrayList<>();

        for (Provider provider : providers)
        {
            names.add(provider.getClass().getSimpleName());
        }

        log.log(String.join(",", names));

        while (true)
        {
            List<Float> values = providers.stream().map(Provider::value).collect(Collectors.toList());
            log.log(values);

            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) throws Exception
    {
        Main main = new Main();
        main.start();
    }
}