package com.github.klefstad_teaching.cs122b.movies.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.movies.config.DetailMovie;
import com.github.klefstad_teaching.cs122b.movies.config.GenericInformation;
import com.github.klefstad_teaching.cs122b.movies.config.Movie;
import com.github.klefstad_teaching.cs122b.movies.config.Person;
import com.github.klefstad_teaching.cs122b.movies.data.MovieOrderBy;
import com.github.klefstad_teaching.cs122b.movies.model.GetMovieByIDResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieRepo
{
    private ObjectMapper objectMapper;
    private final NamedParameterJdbcTemplate template;
    @Autowired
    public MovieRepo(ObjectMapper objectMapper, NamedParameterJdbcTemplate template)
    {
        this.objectMapper = objectMapper;
        this.template = template;

    }

    //language=sql
    private static final String MOVIE_SEARCH_NO_GENRE =
            "SELECT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                    "FROM movies.movie m " +
                    "JOIN movies.person p ON p.id = m.director_id";

    //language=sql
    private static final String MOVIE_SEARCH_GENRE =
            "SELECT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden " +
                    "FROM movies.movie m " +
                    "JOIN movies.person p ON p.id = m.director_id " +
                    "JOIN movies.movie_genre g1 ON g1.movie_id = m.id " +
                    "JOIN movies.genre g ON g.id = g1.genre_id";

    //language=sql
    private static final String MOVIE_SEARCH_PERSON_ID =
            "SELECT m.id, m.title, m.year, p.name, m.rating, m.backdrop_path, m.poster_path, m.hidden\n" +
                    "FROM movies.movie m\n" +
                    "    JOIN movies.movie_person mp on m.id = mp.movie_id\n" +
                    "    JOIN movies.person p on p.id = m.director_id\n" +
                    "    WHERE mp.person_id = :personID";

    //language=sql
    private static final String GET_MOVIE_BY_ID =
            "SELECT m.id, m.title, m.year, p.name, m.rating, m.num_votes, m.budget, m.revenue, m.overview,\n" +
                    "       m.backdrop_path, m.poster_path, m.hidden,\n" +
                    "       (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', c.id, 'name', c.name))\n" +
                    "        FROM (SELECT DISTINCT g.id, g.name\n" +
                    "                FROM movies.movie_genre mg\n" +
                    "                 JOIN movies.genre g ON g.id = mg.genre_id\n" +
                    "                WHERE mg.movie_id = m.id\n" +
                    "             ORDER BY g.name) as c) as jsonGenres,\n" +
                    "       (SELECT JSON_ARRAYAGG(JSON_OBJECT('id', jp.id, 'name', jp.name))\n" +
                    "        FROM (SELECT DISTINCT p.id, p.name, p.popularity\n" +
                    "              FROM movies.movie_person mp\n" +
                    "                 JOIN movies.person p ON p.id = mp.person_id\n" +
                    "              WHERE mp.movie_id = m.id\n" +
                    "              ORDER BY p.popularity DESC, p.id) as jp) as jsonPeople\n" +
                    "FROM movies.movie m\n" +
                    "         JOIN movies.person p on p.id = m.director_id\n" +
                    "WHERE m.id = :movieID ";
    //language=sql
    private static final String PERSON_SEARCH =
            "SELECT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path\n" +
                    "FROM movies.person p\n" +
                    "    JOIN movies.movie_person mp on mp.person_id = p.id\n" +
                    "    JOIN movies.movie m on m.id = mp.movie_id\n";
    //language=sql
    private static final String PERSON_SEARCH_BASIC =
            "SELECT p.id, p.name, p.birthday, p.biography, p.birthplace, p.popularity, p.profile_path\n" +
                    "FROM movies.person p\n";

    public void handlePaging(Integer limit, Integer page, StringBuilder sql,MapSqlParameterSource source)
    {
        int offset = -1;
        if (limit != null && page != null)  //Both of them not null
        {
            offset = (page-1) * limit;
        }
        else if (limit != null)     //Limit != null and page == null
        {
            offset = 0;
        }
        else if (page != null)   //Limit == null and page != null
        {
            offset = (page - 1) * 10;
        }

        if (offset > 0 && limit != null)
        {
            sql.append(" LIMIT :limit OFFSET :offset");
            source.addValue("limit", limit, Types.INTEGER)
                    .addValue("offset", offset, Types.INTEGER);
        }
        else if (limit != null)
        {
            sql.append(" LIMIT :limit OFFSET :offset");
            source.addValue("limit", limit, Types.INTEGER)
                    .addValue("offset", 0, Types.INTEGER);
        }
        else if (offset >= 0)
        {
            sql.append(" LIMIT :limit OFFSET :offset");
            source.addValue("limit", 10, Types.INTEGER)
                    .addValue("offset", offset, Types.INTEGER);
        }
        else
        {
            sql.append(" LIMIT :limit OFFSET :offset");
            source.addValue("limit", 10, Types.INTEGER)
                    .addValue("offset", 0, Types.INTEGER);
        }
    }

    public List<Movie> searchMovies(String title, Integer year, String director, String genre,
                                    Integer limit, Integer page, String orderBy, String direction)
    {
        StringBuilder sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();
        boolean whereAdded = false;

        if (genre != null)
        {
            sql = new StringBuilder(MOVIE_SEARCH_GENRE);
            //WHERE m.title LIKE '%DEVIL%' AND g.name LIKE '%horror%'
            sql.append(" WHERE g.name LIKE :genre ");
            String wildGenre = '%' + genre + '%';
            source.addValue("genre", wildGenre, Types.VARCHAR);
            whereAdded = true;
        }
        else{
            sql = new StringBuilder(MOVIE_SEARCH_NO_GENRE);
        }
//        System.out.println("Title: " + title);
        if (title != null)
        {
            String wildTitle = '%' + title + '%';
            if (whereAdded)
            {
                sql.append(" AND ");
            }
            else
            {
                sql.append(" WHERE ");
                whereAdded = true;
            }
            sql.append(" m.title LIKE :title ");
            source.addValue("title", wildTitle, Types.VARCHAR);

        }
        //Handle the year if is present
        if (year != null)
        {
            if (whereAdded)
            {
                sql.append(" AND ");
            }
            else
            {
                sql.append(" WHERE ");
                whereAdded = true;
            }
            sql.append(" m.year = :year ");
            source.addValue("year", year, Types.INTEGER);
        }

        //Handle the director if is present
        if (director != null)
        {
            if (whereAdded)
            {
                sql.append(" AND ");
            }
            else
            {
                sql.append(" WHERE ");
                whereAdded = true;
            }
            sql.append(" p.name LIKE :director ");
            String wildDirector = '%' + director + '%';
            source.addValue("director", wildDirector, Types.VARCHAR);
        }

        return getMovies(limit, page, sql, source, orderBy, direction);
    }

    public List<Movie> searchMovieByPersonID(Long personID, Integer limit, Integer page,
                                             String orderBy, String direction)
    {
        StringBuilder sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();

        sql = new StringBuilder(MOVIE_SEARCH_PERSON_ID);
        source.addValue("personID", personID, Types.INTEGER);
        return getMovies(limit, page, sql, source, orderBy, direction);
    }

    public GetMovieByIDResponse getMovieByID(Long movieID, List<String> roles)
    {
        GetMovieByIDResponse response = null;
        StringBuilder sql = new StringBuilder(GET_MOVIE_BY_ID);
        if (!(roles.contains("ADMIN") || roles.contains("EMPLOYEE"))){
            sql.append("AND m.hidden = false;");
        }
        else{
            sql.append(";");
        }

        System.out.println("SQL: " + sql);
        System.out.println("MovieId: " + movieID);
        try
        {
            response =
                    this.template.queryForObject(
                           sql.toString(),
                           new MapSqlParameterSource().addValue("movieID", movieID, Types.INTEGER),
                           this::insteadOfLambdaMapping
                    );

        }
        catch (DataAccessException e)
        {
            System.out.println("Return nulls?");
            return null;
        }
        return response;
    }

    public List<Person> searchPersons(String name, String birthday, String movieTitle, Integer limit,
                                      Integer page, String orderBy, String direction)
    {
        StringBuilder sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();
        boolean whereAdded = false;

        if (movieTitle != null)
        {
            sql = new StringBuilder(PERSON_SEARCH);
            sql.append(" WHERE m.title LIKE :moveTitle ");
            String wildGenre = '%' + movieTitle + '%';
            source.addValue("moveTitle", wildGenre, Types.VARCHAR);
            whereAdded = true;
        }
        else{
            sql = new StringBuilder(PERSON_SEARCH_BASIC);
        }

        if (name != null)
        {
            String wildTitle = '%' + name + '%';
            if (whereAdded)
            {
                sql.append(" AND ");
            }
            else
            {
                sql.append(" WHERE ");
                whereAdded = true;
            }
            sql.append(" p.name LIKE :name ");
            source.addValue("name", wildTitle, Types.VARCHAR);
        }

        if (birthday != null)
        {
            LocalDate date = LocalDate.parse(birthday);
            if (whereAdded)
            {
                sql.append(" AND ");
            }
            else
            {
                sql.append(" WHERE ");
                whereAdded = true;
            }
            sql.append(" p.birthday = :date ");
            source.addValue("date", date, Types.DATE);
        }
        return getPersons(limit, page, sql, source, orderBy, direction);
    }

    public Person searchPersonID(Long id)
    {
        Person p = null;
        StringBuilder sql;
        MapSqlParameterSource source     = new MapSqlParameterSource();

        sql = new StringBuilder(PERSON_SEARCH_BASIC);
        sql.append("WHERE p.id = :id");

        try
        {
            p = this.template.queryForObject(
                    sql.toString(),
                    new MapSqlParameterSource().addValue("id", id, Types.INTEGER),
                    (rs, rowNum) ->
                            new Person()
                                    .setId(rs.getLong("id"))
                                    .setName(rs.getString("name"))
                                    .setBirthday(rs.getString("birthday"))
                                    .setBirthplace(rs.getString("birthplace"))
                                    .setBiography(rs.getString("biography"))
                                    .setPopularity(rs.getFloat("popularity"))
                                    .setProfilePath(rs.getString("profile_path"))
                    );
        }
        catch (DataAccessException e)
        {
            return null;
        }
        return p;
    }

    private GetMovieByIDResponse insteadOfLambdaMapping(ResultSet rs, int rowNum) throws SQLException {
        DetailMovie movies = null;
        List<GenericInformation> genres = null;
        List<GenericInformation> persons = null;

        System.out.println("Does it come to lambdaMapping");
        try
        {
            String jsonGenresString = rs.getString("jsonGenres");
            String jsonPeopleString = rs.getString("jsonPeople");

            GenericInformation[] genreClassArray = objectMapper.readValue(jsonGenresString, GenericInformation[].class);
            GenericInformation[] peopleClassArray = objectMapper.readValue(jsonPeopleString, GenericInformation[].class);

            genres = Arrays.stream(genreClassArray).collect(Collectors.toList());
            persons = Arrays.stream(peopleClassArray).collect(Collectors.toList());

        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException("Failed to map to either 'genre' or 'people' to genreClassArray[]/peopleClassArray[]");
        }
        movies =
                new DetailMovie()
                        .setId(rs.getLong("id"))
                        .setDirector(rs.getString("name"))
                        .setYear(rs.getInt("year"))
                        .setTitle(rs.getString("title"))
                        .setRating(rs.getDouble("rating"))
                        .setNumVotes(rs.getLong("num_votes"))
                        .setBudget(rs.getLong("budget"))
                        .setRevenue(rs.getLong("revenue"))
                        .setOverview(rs.getString("overview"))
                        .setBackdropPath(rs.getString("backdrop_path"))
                        .setPosterPath(rs.getString("poster_path"))
                        .setHidden(rs.getBoolean("hidden"));

        return new GetMovieByIDResponse()
                .setMovie(movies)
                .setGenres(genres)
                .setPersons(persons);
    }
    private List<Movie> getMovies(Integer limit, Integer page, StringBuilder sql, MapSqlParameterSource source,
                                  String orderBy, String direction) {
        MovieOrderBy orderBySQL = MovieOrderBy.fromString(orderBy);
        sql.append(orderBySQL.toSql());

        if (direction == null || direction.equalsIgnoreCase("ASC"))
        {
            sql.append("ASC, m.id ASC ");
        }
        else
        {
            sql.append("DESC, m.id ASC ");
        }

        handlePaging(limit, page, sql, source); //Should handle all the paging
        List<Movie> movies;

        System.out.println("MOVIE SQL: " + sql);
        try
        {
            movies = this.template.query(
                    sql.toString(),
                    source,
                    (rs, rowNum) ->
                            new Movie()
                                    .setId(rs.getLong("id"))
                                    .setDirector(rs.getString("name"))
                                    .setYear(rs.getInt("year"))
                                    .setTitle(rs.getString("title"))
                                    .setRating(rs.getDouble("rating"))
                                    .setBackdropPath(rs.getString("backdrop_path"))
                                    .setPosterPath(rs.getString("poster_path"))
                                    .setHidden(rs.getBoolean("hidden"))
            );
        }
        catch (DataAccessException e)
        {
            return new ArrayList<>();
        }
        return movies;
    }

    private List<Person> getPersons(Integer limit, Integer page, StringBuilder sql, MapSqlParameterSource source,
                                  String orderBy, String direction) {
        if (orderBy == null)
        {
            orderBy = "NAME";
        }
        MovieOrderBy orderBySQL = MovieOrderBy.fromString(orderBy);
        sql.append(orderBySQL.toSql());

        if (direction == null)
        {
            sql.append("ASC, p.id ASC\n ");
        }
        else
        {
            sql.append("DESC, p.id ASC\n ");
        }

        handlePaging(limit, page, sql, source); //Should handle all the paging
        List<Person> persons;
        try
        {
            persons = this.template.query(
                    sql.toString(),
                    source,
                    (rs, rowNum) ->
                            new Person()
                                    .setId(rs.getLong("id"))
                                    .setName(rs.getString("name"))
                                    .setBirthday(rs.getString("birthday"))
                                    .setBirthplace(rs.getString("birthplace"))
                                    .setBiography(rs.getString("biography"))
                                    .setPopularity(rs.getFloat("popularity"))
                                    .setProfilePath(rs.getString("profile_path"))
            );
        }
        catch (DataAccessException e)
        {
            return new ArrayList<>();
        }
        return persons;
    }


}
