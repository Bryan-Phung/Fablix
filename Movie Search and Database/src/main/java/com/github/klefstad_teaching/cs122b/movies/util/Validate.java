package com.github.klefstad_teaching.cs122b.movies.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.MoviesResults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Component
public class Validate
{

    List<String> moviesOrderBy = new ArrayList<>(Arrays.asList("title", "rating", "year"));
    List<String> personOrderBy = new ArrayList<>(Arrays.asList("name", "popularity", "birthday"));
    public void validSearchInputs(Integer limit, String orderBy, Integer page, String direction, boolean order)
    {
        if (direction != null && !(direction.equalsIgnoreCase("asc") || direction.equalsIgnoreCase("desc")))
        {
            throw new ResultError(MoviesResults.INVALID_DIRECTION);
        }
        else if (page != null && page <= 0)
        {
            throw new ResultError(MoviesResults.INVALID_PAGE);
        }
        else if (limit != null && !(limit == 10 || limit == 25 || limit == 50 || limit == 100))
        {
            throw new ResultError(MoviesResults.INVALID_LIMIT);
        }
        else if (orderBy != null && orderBy.trim().length() > 0)
        {
            if ((order && !moviesOrderBy.contains(orderBy)) || (!order && !personOrderBy.contains(orderBy)))
            {
                throw new ResultError(MoviesResults.INVALID_ORDER_BY);
            }
        }

    }
}
