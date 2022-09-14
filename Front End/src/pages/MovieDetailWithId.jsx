import React, {useEffect, useState} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {addToCart, movieSearchId} from "backend/idm";
import {useParams} from "react-router-dom";
import {useForm} from "react-hook-form";
import {FaBookmark, FaStar} from "react-icons/fa";

const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`;

const StyledH1 = styled.h1`
    text-align: center;
    font-size : 45px;
    margin-bottom : 20px;
`;

const StyledInput = styled.input`
`;

const StyledButton = styled.button`
`;


const MovieDetailWithId = () => {

    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const [movieInfo, setMovieInfo] = useState([]);
    const {register, getValues, handleSubmit} = useForm();
    const {id} = useParams();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = () => {
        const payLoad = {
            movieId : id,
            accessToken: accessToken
        };

        movieSearchId(payLoad)
            .then(response => {
                // alert(JSON.stringify(response.data, null, 2));
                if (response.data.result["code"] === 2010)
                {
                    setMovieInfo(response.data["movie"]);
                }
                else
                    setMovieInfo([]);
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    };

    const submitAddCart = () => {

        // const quantity = getValues("quantity");
        // console.log("The quantity: " + getValues("quantity"));
        const payLoad = {
            movieId : id,
            quantity : getValues("quantity"),
            accessToken : accessToken
        };

        addToCart(payLoad)
            .then(response => {
                if (response.data.result["code"] === 3010)
                    alert(response.data.result.message);
        })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }

    return (
        <div>
            <StyledH1>Title: {movieInfo.title}</StyledH1>
            {/*<h1>{movieInfo.year}</h1>*/}
            {/*<h1>{movieInfo.director}</h1>*/}
            {/*<h1>Rating: {movieInfo.rating}</h1>*/}
            {/*<h1>Votes: {movieInfo.numVotes}</h1>*/}
            {/*<h1>Budget: {movieInfo.budget}</h1>*/}
            {/*<h1>Revenue: {movieInfo.revenue}</h1>*/}
            {/*<h1>Overview: {movieInfo.overview}</h1>*/}
            <div className="banner-movieDetail" style={{backgroundImage: `url(https://image.tmdb.org/t/p/original${movieInfo.backdropPath})`}}>

                <a href={"/movie/" + movieInfo.id}>
                        <img key={movieInfo.movieId}
                             src={"https://image.tmdb.org/t/p/w300" + movieInfo.posterPath}
                             alt="Temporary img" className="movie-detail-img"/>
                </a>
                <li className="movie-detail-info">{movieInfo.overview}</li>
                <li className="movie-detail-info-year">Year: {movieInfo.year}</li>
                <li className="movie-detail-info-year">Director: {movieInfo.director}</li>
                <div className="movie-detail-info-btn">
                    <li className="movie-detail-info-year">Quantity: </li>
                    <input type={"number"} {...register("quantity")} className="input-number" min="1" max="10" step="1" placeholder="1"/>
                    <button className = "add-button-cart" onClick={handleSubmit(submitAddCart)}>Add</button>
                </div>
            </div>

        </div>
    );
}

export default MovieDetailWithId;


