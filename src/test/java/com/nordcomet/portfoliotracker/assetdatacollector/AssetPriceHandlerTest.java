package com.nordcomet.portfoliotracker.assetdatacollector;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nordcomet.portfoliotracker.assetprice.AssetPriceHandler;
import com.nordcomet.portfoliotracker.assetprice.model.Asset;
import com.nordcomet.portfoliotracker.assetprice.model.AssetPrice;
import com.nordcomet.portfoliotracker.assetprice.model.Price;
import com.nordcomet.portfoliotracker.assetprice.parser.MorningstarParser;
import org.javalite.activejdbc.Base;
import org.junit.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssetPriceHandlerTest {


    @BeforeClass
    public static void setUp() {
        Base.open();
        AssetPrice.deleteAll();
        Asset.deleteAll();

        new Asset()
                .set("name", "Nokia")
                .set("trackingId", UUID.randomUUID().toString())
                .set("trackingSource", "MORNINGSTAR_FI")
                .saveIt();
        new Asset()
                .set("name", "Amazon")
                .set("trackingId", UUID.randomUUID().toString())
                .set("trackingSource", "MORNINGSTAR_FI")
                .saveIt();
        new Asset()
                .set("name", "Huawei")
                .set("trackingId", UUID.randomUUID().toString())
                .set("trackingSource", "MORNINGSTAR_FI")
                .saveIt();
        new Asset()
                .set("name", "Google")
                .set("trackingId", UUID.randomUUID().toString())
                .set("trackingSource", "MORNINGSTAR_FI")
                .saveIt();
        Base.close();
    }

    @Test
    public void invokeCollector() {
        MorningstarParser morningstarParser = mock(MorningstarParser.class);
        when(morningstarParser.parsePrice(anyString())).thenReturn(
                new Price(new BigDecimal("123.51"), "EUR"),
                new Price(new BigDecimal("523.46"), "EUR"),
                new Price(new BigDecimal("10.10"), "EUR"),
                new Price(new BigDecimal("26.57"), "EUR")
        );

        Context contextMock = mock(Context.class);
        LambdaLogger logger = mock(LambdaLogger.class);
        when(contextMock.getLogger()).thenReturn(logger);

        AssetPriceHandler collector = new AssetPriceHandler();
        collector.setMorningstarParser(morningstarParser);
        collector.handleRequest(1, contextMock);

    }

}