package com.github.klefstad_teaching.cs122b.idm.component;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.idm.repo.IDMRepo;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.RefreshToken;
import com.github.klefstad_teaching.cs122b.idm.repo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

@Component
public class IDMAuthenticationManager
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String       HASH_FUNCTION = "PBKDF2WithHmacSHA512";

    private static final int ITERATIONS     = 10000;
    private static final int KEY_BIT_LENGTH = 512;

    private static final int SALT_BYTE_LENGTH = 4;

    public final IDMRepo repo;

    @Autowired
    public IDMAuthenticationManager(IDMRepo repo)
    {
        this.repo = repo;
    }

    private static byte[] hashPassword(final char[] password, String salt)
    {
        return hashPassword(password, Base64.getDecoder().decode(salt));
    }

    private static byte[] hashPassword(final char[] password, final byte[] salt)
    {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_FUNCTION);

            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BIT_LENGTH);

            SecretKey key = skf.generateSecret(spec);

            return key.getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] genSalt()
    {
        byte[] salt = new byte[SALT_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        return salt;
    }

    public User selectAndAuthenticateUser(String email, char[] password) {
        //1. Get the User with a SELECT statement with repo
        //2. CHeck if the User is legit with the SAME salt return!

        User user = repo.getUserByEmail(email);
        byte[] givenPassword = hashPassword(password, user.getSalt());
//        char[] realPassword = user.getHashedPassword().toCharArray();
        String base64EncodedGivenPassword = Base64.getEncoder().encodeToString(givenPassword);

        //If the passwords match, return user
        if (base64EncodedGivenPassword.equals(user.getHashedPassword())) {
            return user;
        }
        else{
            throw new ResultError(IDMResults.INVALID_CREDENTIALS);
        }
    }

    public int createAndInsertUser(String email, char[] password)
    {
        //HAVE EMAIL, PASSWORD
        //1. Create the ID (AUTO-ASSIGNED), email = email, user_status_id = DEFAULT, salt = salt, hashed_password = hashPassword

        byte[] salt = genSalt();

        byte[] hashPassword = hashPassword(password, salt);

        String base64EncodedHashPassword = Base64.getEncoder().encodeToString(hashPassword);
        String base64EncodedSalt = Base64.getEncoder().encodeToString(salt);

        return repo.updateUser(email, 1, base64EncodedSalt, base64EncodedHashPassword);

    }

    public void insertRefreshToken(RefreshToken refreshToken)
    {
        //Adds the token to the database
        repo.addToken(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token)
    {
        return repo.getRefreshToken(token);
    }

    public void updateRefreshTokenExpireTime(RefreshToken token)
    {
        repo.updateExpireRefreshToken(token);
    }

    public void expireRefreshToken(RefreshToken token)
    {
        repo.updateRefreshToken(token);
    }

    public void revokeRefreshToken(RefreshToken token)
    {
        repo.updateRefreshToken(token);
    }

    public User getUserFromRefreshToken(RefreshToken refreshToken)
    {
        return repo.getUserById(refreshToken.getUserId());
    }
}
