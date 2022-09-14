package model;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;

public class PaymentIntentResponse extends ResponseModel<PaymentIntentResponse> {
    private String paymentIntentId;
    private String clientSecret;

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public PaymentIntentResponse setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public PaymentIntentResponse setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
