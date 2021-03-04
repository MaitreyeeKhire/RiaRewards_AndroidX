package com.pegasusgroup.riarewards.model;

/**
 * Created By : Kalpen Vaghela
 */
public class OutofStockListenerData {

    private boolean isOutOfStock;

    public OutofStockListenerData(boolean isOutOfStock) {
        this.isOutOfStock = isOutOfStock;
    }

    public boolean isOutOfStock() {
        return isOutOfStock;
    }
}