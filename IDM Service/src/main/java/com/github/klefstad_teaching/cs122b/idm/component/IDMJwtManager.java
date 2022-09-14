package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.idm.config.IDMServiceConfig;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.Role;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.type.TokenStatus;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@Component
public class IDMJwtManager
{
    private final JWTManager jwtManager;

    @Autowired
    public IDMJwtManager(IDMServiceConfig serviceConfig)
    {
        this.jwtManager =
            new JWTManager.Builder()
                .keyFileName(serviceConfig.keyFileName())
                .accessTokenExpire(serviceConfig.accessTokenExpire())
                .maxRefreshTokenLifeTime(serviceConfig.maxRefreshTokenLifeTime())
                .refreshTokenExpire(serviceConfig.refreshTokenExpire())
                .build();
    }

    private SignedJWT buildAndSignJWT(JWTClaimsSet claimsSet)
        throws JOSEException
    {
        JWSHeader header =
                new JWSHeader.Builder(JWTManager.JWS_ALGORITHM)
                        .keyID(jwtManager.getEcKey().getKeyID())
                        .type(JWTManager.JWS_TYPE)
                        .build();
        return new SignedJWT(header, claimsSet);
    }

    private void verifyJWT(SignedJWT jwt)
        throws JOSEException, BadJOSEException
    {
        try
        {
            jwt.verify(jwtManager.getVerifier());
            jwtManager.getJwtProcessor().process(jwt, null);
        }
        catch (IllegalArgumentException | JOSEException | BadJOSEException e)
        {
            throw new ResultError(IDMResults.ACCESS_TOKEN_IS_INVALID);
        }
    }

    public String buildAccessToken(User user) throws JOSEException {
        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder()
                        .subject(user.getEmail())
                        .expirationTime(
                                Date.from(Instant.now().plus(jwtManager.getAccessTokenExpire()))
                        )
                        .issueTime(Date.from(Instant.now()))
                        .claim(JWTManager.CLAIM_ROLES, user.getRoles())
                        .claim(JWTManager.CLAIM_ID, user.getId())
                        .build();

        SignedJWT signedJWT = buildAndSignJWT(claimsSet);
        signedJWT.sign(jwtManager.getSigner());

        return signedJWT.serialize();
    }

    public void verifyAccessToken(String jws)
    {
        try
        {
            SignedJWT signedJWT = SignedJWT.parse(jws);
            verifyJWT(signedJWT);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            if (Instant.now().isAfter(claimsSet.getExpirationTime().toInstant())){
                throw new ResultError(IDMResults.ACCESS_TOKEN_IS_EXPIRED);
            }

        }
        catch (ParseException | JOSEException | BadJOSEException e) {
            e.printStackTrace();
        }
    }

    public RefreshToken buildRefreshToken(User user)
    {
        return new RefreshToken()
                .setToken(generateUUID().toString())
                .setUserId(user.getId())
                .setTokenStatus(TokenStatus.fromId(1))
                .setExpireTime(Instant.now().plus(jwtManager.getRefreshTokenExpire()))
                .setMaxLifeTime(Instant.now().plus(jwtManager.getMaxRefreshTokenLifeTime()));
    }

    public boolean hasExpired(RefreshToken refreshToken)
    {
        Instant currentTime = Instant.now();
        return (currentTime.isAfter(refreshToken.getExpireTime()) || currentTime.isAfter(refreshToken.getMaxLifeTime()));
    }

    public boolean needsRefresh(RefreshToken refreshToken)
    {
        return refreshToken.getExpireTime().isAfter(refreshToken.getMaxLifeTime());
    }

    public void checkRefreshToken(RefreshToken refreshToken)
    {
        //1. Expired
        if (refreshToken.getTokenStatus().id() == 2)
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_EXPIRED);
        }
        else if (refreshToken.getTokenStatus().id() == 3) //2. REVOKED
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_IS_REVOKED);
        }

    }

    public void updateRefreshTokenExpireTime(RefreshToken refreshToken)
    {
        //4. Update the refreshToken
        refreshToken.setExpireTime(Instant.now().plus(jwtManager.getRefreshTokenExpire()));
    }

    private UUID generateUUID()
    {
        return UUID.randomUUID();
    }
}
