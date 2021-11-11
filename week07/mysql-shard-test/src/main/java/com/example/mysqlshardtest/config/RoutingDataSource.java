package com.example.mysqlshardtest.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Optional;

public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return Context.getSourceKey();
    }

    public static class Context  {
        private static final ThreadLocal<String> sourceKey = new ThreadLocal<>();

        public static void setSourceKey(String sourceKey) {
            Context.sourceKey.set(sourceKey);
        }

        public static String getSourceKey() {
            return Optional.ofNullable(Context.sourceKey.get()).orElse("masterDataSource");
        }

        public static void remove()  {
            sourceKey.remove();
        }
    }
}
