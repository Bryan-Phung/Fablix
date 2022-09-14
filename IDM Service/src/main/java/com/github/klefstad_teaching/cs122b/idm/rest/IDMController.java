package com.github.klefstad_teaching.cs122b.idm.rest;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMAuthenticationManager;
import com.github.klefstad_teaching.cs122b.idm.component.IDMJwtManager;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.Role;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.UserStatus;
import com.github.klefstad_teaching.cs122b.idm.request.authenticateRequest;
import com.github.klefstad_teaching.cs122b.idm.request.refreshRequest;
import com.github.klefstad_teaching.cs122b.idm.request.registerRequest;
import com.github.klefstad_teaching.cs122b.idm.response.AuthenticateResponse;
import com.github.klefstad_teaching.cs122b.idm.response.TokenResponse;
import com.github.klefstad_teaching.cs122b.idm.response.registerResponse;
import com.github.klefstad_teaching.cs122b.idm.util.Validate;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@RestController
public class IDMController
{
    private final IDMAuthenticationManager authManager;
    private final IDMJwtManager            jwtManager;
    private final Validate                 validate;

    @Autowired
    public IDMController(IDMAuthenticationManager authManager,
                         IDMJwtManager jwtManager,
                         Validate validate)
    {
        this.authManager = authManager;
        this.jwtManager = jwtManager;
        this.validate = validate;
    }

    @PostMapping("/register")
    public ResponseEntity<registerResponse> registerUsers(
            @RequestBody registerRequest request
    )
    {

        //Check if the username, password, email, but NOT if email already exist
        //EMAIL exist will be checked in AuthenticationManger
        String email = request.getEmail();
        char[] password = request.getPassword();    //MIGHT BE CHARACTER[] IF SOME THINGS HAPPEN

        //Check for errors within email/password
        validate.validUser(email, password);

        int val = authManager.createAndInsertUser(email, password);

        //Rows got updated (was added to the query)
        if (val > 0)
        {
            registerResponse rep = new registerResponse()
                    .setResult(IDMResults.USER_REGISTERED_SUCCESSFULLY);
            return rep.toResponse();
        }
        else{
            throw new ResultError(IDMResults.USER_ALREADY_EXISTS);
        }

//            !Arrays.toString(password).matches("/\\S+@\\S+\\.\\S+/")

    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginUsers(
            @RequestBody registerRequest request
    ) throws JOSEException {
        String email = request.getEmail();
        char[] password = request.getPassword();    //MIGHT BE CHARACTER[] IF SOME THINGS HAPPEN

        //Valid the email/passsword
        validate.validUser(email, password);

        //1. Check if the user actually exist from the data base, return the User
        User user = authManager.selectAndAuthenticateUser(email , password);

        //2. Check if the user is even allowed to begin with
        if (user.getUserStatus().value().equals("Locked"))
        {
            throw new ResultError(IDMResults.USER_IS_LOCKED);
        }
        else if (user.getUserStatus().value().equals("Banned"))
        {
            throw new ResultError(IDMResults.USER_IS_BANNED);
        }

        //3. Use the claimSet, etc(don't know)
        //4. Call the manager to create the header and return the signedJWT
        String accessToken = jwtManager.buildAccessToken(user);
        RefreshToken refreshToken = jwtManager.buildRefreshToken(user);

        authManager.insertRefreshToken(refreshToken);
        TokenResponse rep = new TokenResponse()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken.getToken())
                .setResult(IDMResults.USER_LOGGED_IN_SUCCESSFULLY);
        return rep.toResponse();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            @RequestBody refreshRequest request
    ) throws JOSEException
    {
        String refreshTokenStr = request.getRefreshToken();

        //At first verify if it is a refreshToken
        validate.validTokenFormat(refreshTokenStr);

        //2. Get the refresh token using authManager
        RefreshToken refreshToken = authManager.verifyRefreshToken(refreshTokenStr);

        //3. Check if the refreshToken is Expired or Revoked (if can be updated, update the refresh token)
        jwtManager.checkRefreshToken(refreshToken);

        if (jwtManager.hasExpired(refreshToken))  //3. currentTime is after refreshToken.expire or refreshToken.maxExpireTime
        {
            authManager.updateRefreshTokenExpireTime(refreshToken.setTokenStatus(TokenStatus.EXPIRED));
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }

        jwtManager.updateRefreshTokenExpireTime(refreshToken);
        authManager.updateRefreshTokenExpireTime(refreshToken);
        User user = authManager.getUserFromRefreshToken(refreshToken);
        TokenResponse rep = new TokenResponse();
        if (jwtManager.needsRefresh(refreshToken))
        {
            refreshToken.setTokenStatus(TokenStatus.REVOKED);
            authManager.revokeRefreshToken(refreshToken);
            String accessToken = jwtManager.buildAccessToken(user);
            RefreshToken newRefreshToken = jwtManager.buildRefreshToken(user);
            authManager.insertRefreshToken(newRefreshToken);

            rep.setRefreshToken(newRefreshToken.getToken())
                    .setAccessToken(accessToken);
        }
        else
        {
            String accessToken = jwtManager.buildAccessToken(user);
            rep.setRefreshToken(refreshToken.getToken())
                    .setAccessToken(accessToken);
        }
        return rep.setResult(IDMResults.RENEWED_FROM_REFRESH_TOKEN)
                .toResponse();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticateToken(
            @RequestBody authenticateRequest request
    )
    {
        String accessToken = request.getAccessToken();

        jwtManager.verifyAccessToken(accessToken);


        return new AuthenticateResponse()
                .setResult(IDMResults.ACCESS_TOKEN_IS_VALID)
                .toResponse();
    }


}
