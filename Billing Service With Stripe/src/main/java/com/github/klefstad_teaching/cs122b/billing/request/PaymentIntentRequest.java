package com.github.klefstad_teaching.cs122b.billing.request;

public class PaymentIntentRequest {
    private String paymentIntentId;

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public PaymentIntentRequest setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        return this;
    }
}
