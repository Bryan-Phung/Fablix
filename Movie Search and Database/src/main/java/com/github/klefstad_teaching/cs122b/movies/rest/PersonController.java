package com.github.klefstad_teaching.cs122b.movies.rest;

import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import com.github.klefstad_teaching.cs122b.movies.config.Person;
import com.github.klefstad_teaching.cs122b.movies.model.PersonIDSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.model.PersonSearchResponse;
import com.github.klefstad_teaching.cs122b.movies.repo.MovieRepo;
import com.github.klefstad_teaching.cs122b.movies.request.GetPersonIDRequest;
import com.github.klefstad_teaching.cs122b.movies.request.PersonSearchRequest;
import com.github.klefstad_teaching.cs122b.movies.util.Validate;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PersonController
{
    private final MovieRepo repo;
    private final Validate validate;

    @Autowired
    public PersonController(MovieRepo repo, Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }


    @GetMapping("/person/search")
    public ResponseEntity<PersonSearchResponse> personSearch(
            @AuthenticationPrincipal SignedJWT jwt,
            PersonSearchRequest request
    )
    {
        String name = request.getName();
        String birthday = request.getBirthday();
        String movieTitle = request.getMovieTitle();
        Integer limit = request.getLimit();
        Integer page = request.getPage();
        String orderBy = request.getOrderBy();
        String direction = request.getDirection();

        //Handle other errors exception orderBy
        validate.validSearchInputs(limit, orderBy, page, direction, false);

        List<Person> persons = repo.searchPersons(name, birthday, movieTitle, limit, page, orderBy, direction);
        PersonSearchResponse response = new PersonSearchResponse();
        if (persons.isEmpty())
        {
            return response.setResult(MoviesResults.NO_PERSONS_FOUND_WITHIN_SEARCH)
                    .toResponse();
        }

        //Returns the response of the movieResults
        return response.setResult(MoviesResults.PERSONS_FOUND_WITHIN_SEARCH)
                .setPersons(persons)
                .toResponse();
    }

    @GetMapping("person/{personId}")
    public ResponseEntity<PersonIDSearchResponse> personSearchID(
        @AuthenticationPrincipal SignedJWT jwt,
        GetPersonIDRequest request
    )
    {
        Person p = repo.searchPersonID(request.getPersonId());
        PersonIDSearchResponse response = new PersonIDSearchResponse();
        if (p == null)
        {
            return response.setResult(MoviesResults.NO_PERSON_WITH_ID_FOUND)
                    .toResponse();
        }
        return response.setResult(MoviesResults.PERSON_WITH_ID_FOUND)
                .setPerson(p)
                .toResponse();
    }
}
