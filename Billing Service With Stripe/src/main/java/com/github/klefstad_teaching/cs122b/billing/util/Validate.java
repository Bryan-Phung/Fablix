package com.github.klefstad_teaching.cs122b.billing.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.stripe.model.PaymentIntent;
import com.stripe.model.issuing.Cardholder;
import org.springframework.stereotype.Component;

@Component
public final class Validate
{
    public void checkCartInputs(Integer quantity)
    {
        if (quantity <= 0)
        {
            throw new ResultError(BillingResults.INVALID_QUANTITY);
        }
        else if (quantity > 10)
        {
            throw new ResultError(BillingResults.MAX_QUANTITY);
        }
    }

    public void checkValidateOrderComplete(Long userId, PaymentIntent paymentIntent)
    {
        if (!paymentIntent.getStatus().equalsIgnoreCase("succeeded"))
        {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }
        else if (!paymentIntent.getMetadata().get("userId").equals(Long.toString(userId)))
        {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_WRONG_USER);
        }
    }
}
