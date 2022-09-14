package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.request.PaymentIntentRequest;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import model.CartRetrieveResponse;
import model.OrderCompleteResponse;
import model.OrderListResponse;
import model.PaymentIntentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
public class OrderController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public OrderController(BillingRepo repo,Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }


    @GetMapping("/order/payment")
    public ResponseEntity<PaymentIntentResponse> paymentIntent(
            @AuthenticationPrincipal SignedJWT jwt
    ) throws ParseException {
        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        List<String> roles = jwt.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        //Call the repo which has the response
        return repo.getPaymentIntent(userId, roles.contains("PREMIUM"), (!(roles.contains("ADMIN") || roles.contains("EMPLOYEE"))))
                .toResponse();
    }

    @PostMapping("/order/complete")
    public ResponseEntity<OrderCompleteResponse> orderComplete(
            @AuthenticationPrincipal SignedJWT jwt,
            @RequestBody PaymentIntentRequest request
    ) throws StripeException, ParseException {
        String paymentIntentId = request.getPaymentIntentId();
//        System.out.println("The paymentIntentId: " + request.getPaymentIntentId());

        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        List<String> roles = jwt.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        PaymentIntent retrievedPaymentIntent = PaymentIntent.retrieve(paymentIntentId);

        //Check if the order can be finished
        validate.checkValidateOrderComplete(userId, retrievedPaymentIntent);

        //Clear the status
        repo.populateSaleOrders(userId, roles.contains("PREMIUM"), (!(roles.contains("ADMIN") || roles.contains("EMPLOYEE"))));

        return new OrderCompleteResponse()
                .setResult(BillingResults.ORDER_COMPLETED)
                .toResponse();
    }

    @GetMapping("/order/list")
    public ResponseEntity<OrderListResponse> orderList (
            @AuthenticationPrincipal SignedJWT jwt
    ) throws ParseException {
        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

//        System.out.println("userId: "+ userId);
        //Get the response from the repo
        return repo.getOrderList(userId)
                .toResponse();
    }

    @GetMapping("order/detail/{saleId}")
    public ResponseEntity<CartRetrieveResponse> orderDetails(
            @AuthenticationPrincipal SignedJWT jwt,
            @PathVariable Long saleId
    ) throws ParseException {
        List<String> roles = jwt.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        //Clear the status
        return repo.getSaleDetails(userId, saleId, roles.contains("PREMIUM"), (!(roles.contains("ADMIN") || roles.contains("EMPLOYEE"))))
                .toResponse();
//        return null;
    }
}
