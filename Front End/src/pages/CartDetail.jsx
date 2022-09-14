import React, {useEffect, useState} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {cartMovieDelete, cartRetrieve, checkOut, movieSearch, movieSearchId, updateCart} from "backend/idm";
import {Dropdown} from "react-bootstrap";
import {FaTrash} from 'react-icons/fa';
import {useNavigate} from "react-router-dom";


const StyledButton = styled.button`
    background-color: white;
    color : #002147;
    width: 200px;
    height : 50px;
    font-size: 20px;
    border-radius: 10px;
`

const CartDetail = () =>
{
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    useEffect(() => {
        fetchData();
    }, []);
    const [posting, setPosting] = useState([]);
    const [total, setTotal] = useState(0.0);
    const navigate = useNavigate();

    const handleMovieCartUpdates = (event) =>
    {
        const selectedIndex = event.target.options.selectedIndex+1;
        // console.log("selectedIndex: " + selectedIndex);
        // console.log(event.target.options[selectedIndex].getAttribute('data-key'));
        const payLoad = {
            accessToken : accessToken,
            movieId : event.target.options[selectedIndex].getAttribute('data-key'),
            quantity : selectedIndex
        };

        updateCart(payLoad)
            .then(response => {
                    if (response.data.result["code"] === 3020)
                        fetchData();
                })
                .catch(error => alert(JSON.stringify(error.response.data, null, 2)));

    };

    const handleOneMovieDelete = (movieId) =>
    {

        // const selectedIndex = event.target.options.selectedIndex;
        // console.log(event.target.options[selectedIndex].getAttribute('data-key'));
        const payLoad = {
            accessToken : accessToken,
            movieId : movieId
        };

        cartMovieDelete(payLoad)
            .then(response => {
                if (response.data.result["code"] === 3030)
                    fetchData();
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
    }

    const fetchData = () => {

        const payLoad = {
            accessToken: accessToken
        };

        cartRetrieve(payLoad)
            .then(response => {
                // alert(JSON.stringify(response.data, null, 2));
                if (response.data.result["code"] === 3040)
                {
                    setPosting(response.data["items"]);
                    setTotal(response.data["total"]);
                }
                else if (response.data.result["code"] === 3004)
                {
                    setPosting([]);
                    setTotal(0);
                }
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    };

    const orderPayment = () =>
    {
        navigate("/order/payment");
    }
    return (
        <div>
            {posting && Object.entries(posting).map(([movieId, post]) => {
                    return (<div key={post.movieId}>
                        <h3 className="card-title">{post.movieTitle}</h3>
                        <li>Unit Price: {post.unitPrice}</li>
                            <div>
                                <li>Cart Items: </li>
                            <select className="filter-dropdowns" defaultValue={post.quantity} onChange={handleMovieCartUpdates}>
                                <option key="1" data-key={post.movieId}>1</option>
                                <option key="2" data-key={post.movieId}>2</option>
                                <option key="3" data-key={post.movieId}>3</option>
                                <option key="4" data-key={post.movieId}>4</option>
                                <option key="5" data-key={post.movieId}>5</option>
                                <option key="6" data-key={post.movieId}>6</option>
                                <option key="7" data-key={post.movieId}>7</option>
                                <option key="8" data-key={post.movieId}>8</option>
                                <option key="9" data-key={post.movieId}>9</option>
                                <option key="10" data-key={post.movieId}>10</option>
                            </select>
                        <FaTrash classname="justPointer" onClick={(event) => handleOneMovieDelete(post.movieId)}/>
                        <img src={"https://image.tmdb.org/t/p/w500" + post.backdropPath} alt={post.movieId}/>
                            </div>
                    </div>);
                })}
            <div>
                {total === 0 && <li>Cart is Empty...</li>}
                {total !== 0 && <li>Total: {total.toFixed(2)} </li>}
                {total !== 0 && <StyledButton onClick={() => orderPayment()}> Proceed to checkout </StyledButton>}
            </div>
        </div>

    );
}

export default CartDetail;