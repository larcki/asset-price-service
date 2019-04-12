package com.nordcomet.portfoliotracker.assetprice.service;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nordcomet.portfoliotracker.assetprice.model.Asset;
import com.nordcomet.portfoliotracker.assetprice.model.AssetPrice;
import com.nordcomet.portfoliotracker.assetprice.model.Price;
import com.nordcomet.portfoliotracker.assetprice.parser.MorningstarParser;

import java.sql.Timestamp;
import java.time.Instant;

public class AssetPriceService {

    private static final String MORNINGSTAR_FI = "MORNINGSTAR_FI";

    private LambdaLogger logger;
    private MorningstarParser morningstarParser;

    public AssetPriceService(LambdaLogger logger, MorningstarParser morningstarParser) {
        this.logger = logger;
        this.morningstarParser = morningstarParser;
    }

    public void updatePrice(Asset asset) {
        Price price = getPrice(asset);
        if (price == null) {
            logger.log("Could not get price for asset " + asset.getId());
            return;
        }
        saveAssetPrice(asset, price);
    }

    private Price getPrice(Asset asset) {
        String trackingSource = asset.getString("trackingSource");
        String trackingId = asset.getString("trackingId");

        if (MORNINGSTAR_FI.equals(trackingSource)) {
            return morningstarParser.parsePrice(trackingId);
        } else {
            logger.log("Tracking source not supported: " + trackingSource);
            return null;
        }
    }

    private void saveAssetPrice(Asset asset, Price price) {
        new AssetPrice()
                .set("timestamp", Timestamp.from(Instant.now()))
                .set("assetId", asset.getId())
                .set("price", price.getPrice())
                .set("currency", price.getCurrency())
                .saveIt();
    }
}
