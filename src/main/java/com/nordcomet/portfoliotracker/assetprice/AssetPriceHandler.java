package com.nordcomet.portfoliotracker.assetprice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nordcomet.portfoliotracker.assetprice.model.Asset;
import com.nordcomet.portfoliotracker.assetprice.service.AssetPriceService;
import com.nordcomet.portfoliotracker.assetprice.parser.MorningstarParser;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

public class AssetPriceHandler implements RequestHandler<Integer, String> {

    private MorningstarParser morningstarParser;
    private AssetPriceService assetPriceService;

    @Override
    public String handleRequest(Integer count, Context context) {
        LambdaLogger logger = context.getLogger();
        initializeServices(logger);

        try {
            Base.open();
            LazyList<Asset> assets = Asset.findAll();
            assets.forEach(assetPriceService::updatePrice);
        } finally {
            Base.close();
        }

        return "DONE";
    }

    private void initializeServices(LambdaLogger logger) {
        if (morningstarParser == null) {
            morningstarParser = new MorningstarParser();
        }
        if (assetPriceService == null) {
            assetPriceService = new AssetPriceService(logger, morningstarParser);
        }
    }

    public void setMorningstarParser(MorningstarParser morningstarParser) {
        this.morningstarParser = morningstarParser;
    }

    public void setAssetPriceService(AssetPriceService assetPriceService) {
        this.assetPriceService = assetPriceService;
    }

}
