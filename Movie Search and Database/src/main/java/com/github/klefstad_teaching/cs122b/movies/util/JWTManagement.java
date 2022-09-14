package com.github.klefstad_teaching.cs122b.movies.util;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.config.Movie;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

@Component
public class JWTManagement {


    public List<Movie> handleHiddenMovies(SignedJWT jwt, List<Movie> movies) throws ParseException {
        //Handle the JWT given
        List<String> claimSet = jwt.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);

        if (!(claimSet.contains("ADMIN") || claimSet.contains("EMPLOYEE")))
        {
            movies.removeIf(Movie::getHidden);
        }
        return movies;
    }


}
