import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {movieSearch} from "backend/idm";
import {DropdownButton, Dropdown, ButtonGroup, Button} from "react-bootstrap";
import 'react-dropdown';
import {useNavigate} from "react-router-dom";
import {FaBookmark, FaStar} from "react-icons/fa";
// import 'react-dropdown/style.css';


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const StyledH1 = styled.h1`
    display: inline;
    padding: 55px;
    font-size: 16px; 
    text-indent: 20%;
`

const StyledInput = styled.input`
`

const StyledButton = styled.button`
`

// class TitleForm extends React.Component{
//
// }

const MovieSearch = () => {

    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const [movies, setMovies] = React.useState([]);
    const [firstPage, setFirstPages] = React.useState(0);
    const [nextPage, setNextPages] = React.useState(10);
    const {register, getValues, handleSubmit} = useForm();
    const [disableForward, setDisableForward] = React.useState(false);
    const [disableBack, setDisableBack] = React.useState(true);

    const submitMovieSearch = () => {
        const title = getValues("title");
        const year = getValues("year");
        const director = getValues("director");
        const genre = getValues("genre");
        const limit = getValues("limit");
        const page = getValues("page");
        const orderBy = getValues("orderBy");
        const direction = getValues("direction");

        const payLoad = {
            title: (title === "") ? null : title,
            year: (year === "") ? null : year,
            director: (director === "") ? null : director,
            genre: (genre === "") ? null : genre,
            limit: (limit === "") ? null : limit,
            page: (page === "") ? null : page,
            orderBy: (orderBy === "") ? null : orderBy,
            direction: (direction === "") ? null : direction,
            accessToken: accessToken
        };

        movieSearch(payLoad)
            .then(response => {
                // alert(JSON.stringify(response.data, null, 2));
                if (response.data.result["code"] === 2020)
                    setMovies(response.data["movies"]);
                else
                    setMovies([]);
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
    }

    return (

        <div key="MovieSearch">
          <h1>Movie Search:</h1>
          <div key="movieTitleInput">
              <label htmlFor="title">Title</label>
          <input id = "title" placeholder="Enter a title..." {...register("title")} type={"text"}/>
              <label htmlFor="year">Year</label>
          <input id = "year" placeholder="Enter a year..." {...register("year")} type={"text"}/>
              <label htmlFor="director">Director</label>
          <input id = "director" placeholder="Enter a director..." {...register("director")} type={"text"}/>
              <label htmlFor="genre">Genre</label>
          <input id = "genre" placeholder="Enter a genre..." {...register("genre")} type={"text"}/>
              <label htmlFor="pages">Pages</label>
          <input type={"number"} {...register("page")} className="size-14" min="1" max="100" step="1" placeholder="1"/>
          {/*    <inputScroll type="number" value = "1"/>*/}
          <select defaultValue = "10" {...register("limit")}>
              <option value={10}>10</option>
              <option value={25}>25</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
          </select>

          <select defaultValue = "asc" {...register("direction")}>
              <option value={"asc"}>Ascending</option>
              <option value={"desc"}>Descending</option>
          </select>

          <select defaultValue = "title" {...register("orderBy")}>
              <option value={"title"}>Title</option>
              <option value={"rating"}>Rating</option>
              <option value={"direction"}>Direction</option>
          </select>

          <button onClick={handleSubmit(submitMovieSearch)}>Search</button>

              <button hidden = {disableForward} onClick={() => {
                  console.log("Movies.length: " + Math.floor(movies.length/10));
                  console.log("Next page: " + nextPage);
                  if (Math.floor(nextPage/10) >= Math.floor(movies.length/10))
                  {
                      setDisableForward(true);
                      setDisableBack(false);
                      return;
                  }
                  if (Math.floor(firstPage/10) > 1)
                  {
                      setDisableBack(false);
                  }
                  setFirstPages(firstPage+10);
                  setNextPages(nextPage+10);
                  setMovies(movies);
              }}>Forward</button>
              <button hidden = {disableBack} onClick={() =>{
                  console.log("Movies.length: " + movies.length);
                  if (Math.floor(firstPage/10) === 1)
                  {
                    setDisableBack(true);
                    setDisableForward(false);
                    return;
                  }
                  if (Math.floor(nextPage/10) <= Math.floor(movies.length/10))
                  {
                      setDisableForward(false);
                  }
                  setFirstPages(firstPage-10);
                  setNextPages(nextPage-10);
                  setMovies(movies);
              }}>Back</button>
        </div>
            <div className="container">
                {movies && movies.map((movie) => {
                    return (<div key={movie.movieId}>
                            <div key={movie.movieId} className="movies-grid">
                                <div key={movie.movieId} className="movie-card">
                                    <div key={movie.movieId} className="card-head">
                                        <a href={"/movie/" + movie.id}>
                                            <img key={movie.movieId}
                                            src={"https://image.tmdb.org/t/p/w400" + movie.posterPath}
                                            alt="Temporary img" className="card-img"/>
                                        <div key={movie.movieId} className="card-overlay">
                                            <div key={movie.movieId} className="bookmark">
                                                <FaBookmark/>
                                            </div>
                                            <div key={movie.movieId} className="rating">
                                                <FaStar key={movie.movieId} />
                                                <span key={movie.movieId}>{movie.rating}</span>
                                            </div>
                                        </div>
                                        <div key={movie.movieId} className="card-body">
                                            <h3 key={movie.movieId} className="card-title">
                                                {movie.title}
                                            </h3>
                                        </div>
                                        <div key={movie.movieId} className="card-info">
                                            <span key={movie.movieId} className="genre">{movie.genre}</span>
                                            <span key={movie.movieId} className="year">{movie.year}</span>
                                        </div>

                                        </a>

                                    </div>
                                </div>
                            </div>
                    </div>);
                })}
             </div>
        </div>
    );
}
export default MovieSearch;

// <div className="container">
//     <div className="cell cell-1">1.</div>
//     <div className="cell cell-2">2.</div>
//     <div className="cell cell-3">3.</div>
//     <div className="cell cell-4">4.</div>
//     <div className="cell cell-5">5.</div>
//     <div className="cell cell-6">6.</div>
//     <div className="cell cell-7">7.</div>
//     <div className="cell cell-8">8.</div>
//     <div className="cell cell-9">9.</div>
// </div>
// <StyledInput {...register("orderBy")} type={"text"}/>
// console.log("Year: " + year);
// console.log("Title: " + title);
// console.log("Director: " + director);
// console.log("Genre: " + genre);
// console.log("Limit: " + limit);
// console.log("Page: " + page);
// console.log("OrderBy: " + orderBy);
// console.log("Direction: " + direction);
//
// <Dropdown>
//     <Dropdown.Toggle variant="success" id="dropdown-basic">
//         Dropdown Button
//     </Dropdown.Toggle>
//
//     <Dropdown.Menu>
//         <Dropdown.Item href="#/action-1">Increasing</Dropdown.Item>
//         <Dropdown.Item href="#/action-2">Decreasing</Dropdown.Item>
//     </Dropdown.Menu>
// </Dropdown>