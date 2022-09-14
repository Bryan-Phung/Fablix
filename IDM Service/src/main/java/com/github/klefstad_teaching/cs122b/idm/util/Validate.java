package com.github.klefstad_teaching.cs122b.idm.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class Validate
{

    public void validUser(String email, char[] password)
    {
        if (password.length < 10 || password.length > 20) //Too short or too long
        {   throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_LENGTH_REQUIREMENTS);
        }
        else if (!String.valueOf(password).matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) //String does not have One Cap, One Lower, One num
        {
            throw new ResultError(IDMResults.PASSWORD_DOES_NOT_MEET_CHARACTER_REQUIREMENT);
        }
        if (email.length() < 6 || email.length() > 32)  //INVALID LENGTH of EMAIL
        {   throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_LENGTH);
        }
        else if (!email.matches("^(.+)@(.+)[.](.+)$"))  //String is not formatted as an email
        {
            throw new ResultError(IDMResults.EMAIL_ADDRESS_HAS_INVALID_FORMAT);
        }
    }

    public void validTokenFormat(String refreshToken)
    {
        if (refreshToken.length() != 36)
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_LENGTH);
        }
        try
        {
            UUID i = UUID.fromString(refreshToken);
        }
        catch (IllegalArgumentException e)
        {
            throw new ResultError(IDMResults.REFRESH_TOKEN_HAS_INVALID_FORMAT);
        }
    }


}
