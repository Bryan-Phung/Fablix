package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.request.CartRequest;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import model.CartResponse;
import model.CartRetrieveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
public class CartController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public CartController(BillingRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @PostMapping("/cart/insert")
    public ResponseEntity<CartResponse> insertMovie(
            @RequestBody CartRequest request,
            @AuthenticationPrincipal SignedJWT jwt
    ) throws ParseException {
        Long movieId = request.getMovieId();
        Integer quantity = request.getQuantity();

        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

        //Validate the information
        validate.checkCartInputs(quantity);

        //Repo insert the movies to the db
        repo.insertMovies(userId, movieId, quantity);

//        System.out.println("Does it reach here?");
        //If it had reached here insert has worked
        return new CartResponse()
                .setResult(BillingResults.CART_ITEM_INSERTED)
                .toResponse();
    }

    @PostMapping("/cart/update")
    public ResponseEntity<CartResponse> updateMovie(
            @RequestBody CartRequest request,
            @AuthenticationPrincipal SignedJWT jwt
    ) throws ParseException {

        Long movieId = request.getMovieId();
        Integer quantity = request.getQuantity();
        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);


        //Validate the information
        validate.checkCartInputs(quantity);

        //Call the repo to update the db
        repo.updateMovies(userId, movieId, quantity);

        return new CartResponse()
                .setResult(BillingResults.CART_ITEM_UPDATED)
                .toResponse();
    }

    @DeleteMapping("/cart/delete/{movieId}")
    public ResponseEntity<CartResponse> deleteMovie(
            @PathVariable Long movieId,
            @AuthenticationPrincipal SignedJWT jwt
    ) throws ParseException
    {
        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);

        //Repo handles to delete
        repo.deleteMovieFromCart(userId, movieId);

        //Response
        return new CartResponse()
                .setResult(BillingResults.CART_ITEM_DELETED)
                .toResponse();
    }

    @GetMapping("/cart/retrieve")
    public ResponseEntity<CartRetrieveResponse> retrieveMovies(
            @AuthenticationPrincipal SignedJWT jwt
    ) throws ParseException
    {
        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        List<String> roles = jwt.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
//        System.out.println("The roles: " + roles);

        //True == PREMIUM USER (Discount) , False == No discount
        return repo.retrieveMovies(userId, roles.contains("PREMIUM"), (!(roles.contains("ADMIN") || roles.contains("EMPLOYEE"))))
                .toResponse();

        //Response
//        return new CartRetrieveResponse()
//                .setResult(BillingResults.CART_RETRIEVED)
//                .toResponse();
    }

    @PostMapping("/cart/clear")
    public ResponseEntity<CartResponse> clearCart(
            @AuthenticationPrincipal SignedJWT jwt
    ) throws ParseException {
        Long userId = jwt.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID);
        return repo.clearCart(userId)
                .toResponse();
    }
}
