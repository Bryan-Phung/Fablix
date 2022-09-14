package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.IDMResults;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.github.klefstad_teaching.cs122b.movies.config.Movie;
import com.github.klefstad_teaching.cs122b.movies.config.Person;
import com.github.klefstad_teaching.cs122b.movies.model.GetMovieByIDResponse;
import com.github.klefstad_teaching.cs122b.movies.model.MovieSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.model.PersonSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.request.MovieSearchPersonIDRequest;
import com.github.klefstad_teaching.cs122b.movies.request.MovieSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.request.getMovieIDRequest;
import com.github.klefstad_teaching.cs122b.movies.util.JWTManagement;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MovieController
{
    private final MovieRepo repo;
    private final Validate validate;
    private final JWTManagement manageJWT;

    @Autowired
    public MovieController(MovieRepo repo, Validate validate, JWTManagement manageJWT)
    {
        this.repo = repo;
        this.validate = validate;
        this.manageJWT = manageJWT;
    }

    @GetMapping("/movie/search")
    public ResponseEntity<MovieSearchResponse> searchMovie(
            @AuthenticationPrincipal SignedJWT jwt,
            MovieSearchRequest request
    ) throws ParseException {
        String title = request.getTitle();
        Integer year = request.getYear();
        String director = request.getDirector();
        String genre = request.getGenre();
        Integer limit = request.getLimit();
        Integer page = request.getPage();
        String orderBy = request.getOrderBy();
        String direction = request.getDirection();

        System.out.println("Title: " + title);
        System.out.println("The Year: " + year);

//        System.out.println("Does it come here: " );

        //Handle other errors exception orderBy (I think handle orderBy now)
        validate.validSearchInputs(limit, orderBy, page, direction, true);

        //Call your repo to handle the query
        List<Movie> info = repo.searchMovies(title, year, director, genre, limit, page,
                orderBy, direction);
        MovieSearchResponse response = new MovieSearchResponse();
        if (info.isEmpty())
        {
            return response.setResult(MoviesResults.NO_MOVIES_FOUND_WITHIN_SEARCH)
                    .toResponse();
        }

        //Handle the JWT given, if hidden or not hidden, manageJWT finish
        info = manageJWT.handleHiddenMovies(jwt, info);

        System.out.println("MovieSearch: " + info);
        //Returns the response of the movieResults
        return response.setResult(MoviesResults.MOVIES_FOUND_WITHIN_SEARCH)
                .setMovies(info)
                .toResponse();
    }

    @GetMapping("/movie/search/person/{personID}")
    public ResponseEntity<MovieSearchResponse> searchMovieByPersID(
            @AuthenticationPrincipal SignedJWT jwt,
            MovieSearchPersonIDRequest request
    ) throws ParseException {
        Long personID = request.getPersonID();
        Integer limit = request.getLimit();
        Integer page = request.getPage();
        String orderBy = request.getOrderBy();
        String direction = request.getDirection();

        //Handle other errors exception orderBy (I think handle orderBy now)
        validate.validSearchInputs(limit, orderBy, page, direction, true);

        List<Movie> info = repo.searchMovieByPersonID(personID, limit, page, orderBy, direction);
        MovieSearchResponse response = new MovieSearchResponse();
        if (info.isEmpty())
        {
            return response.setResult(MoviesResults.NO_MOVIES_WITH_PERSON_ID_FOUND)
                    .toResponse();
        }

        //Handle the JWT given, if hidden or not hidden, manageJWT finish
        info = manageJWT.handleHiddenMovies(jwt, info);

        //Returns the response of the movieResults
        return response.setResult(MoviesResults.MOVIES_WITH_PERSON_ID_FOUND)
                .setMovies(info)
                .toResponse();
    }

    @GetMapping("/movie/{movieID}")
    public ResponseEntity<GetMovieByIDResponse> getMovieByID(
            @AuthenticationPrincipal SignedJWT jwt,
            getMovieIDRequest request
    ) throws ParseException {

        List<String> claimSet = jwt.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        GetMovieByIDResponse response = repo.getMovieByID(request.getMovieID(), claimSet);

        if (response == null)
        {
            return new GetMovieByIDResponse().setResult(MoviesResults.NO_MOVIE_WITH_ID_FOUND)
                    .toResponse();
        }
        return response.setResult(MoviesResults.MOVIE_WITH_ID_FOUND)
                .toResponse();
    }
}
