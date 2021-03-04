package com.pegasusgroup.riarewards.model;

/**
 * Created by Jijo C J on 10,February,2019
 */
public class SendPaymentData {

    private float totalAmount;
    private float totalRequiredPoint;
    private float totalAmountPayByCash;

    public SendPaymentData(float totalAmount, float totalRequiredPoint, float totalAmountPayByCash) {
        this.totalAmount = totalAmount;
        this.totalRequiredPoint = totalRequiredPoint;
        this.totalAmountPayByCash = totalAmountPayByCash;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public float getTotalRequiredPoint() {
        return totalRequiredPoint;
    }

    public float getTotalAmountPayByCash() {
        return totalAmountPayByCash;
    }
}
