package com.nordcomet.portfoliotracker.assetprice;

import org.javalite.activeweb.AbstractDBConfig;
import org.javalite.activeweb.AppContext;

public class DbConfig extends AbstractDBConfig {
    @Override
    public void init(AppContext appContext) {
        configFile("/database.properties");
        environment("production", true).jndi("jdbc/portfoliotracker");
    }
}
