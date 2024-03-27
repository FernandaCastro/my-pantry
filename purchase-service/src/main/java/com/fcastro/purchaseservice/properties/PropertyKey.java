package com.fcastro.purchaseservice.properties;

public enum PropertyKey {
    PRODUCT_CATEGORIES("product.categories"),
    SUPERMARKET_CATEGORIES("supermarket.categories");

    public final String key;

    PropertyKey(String key) {
        this.key = key;
    }
}
