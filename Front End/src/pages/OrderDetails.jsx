import React, {useEffect, useState} from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {addToCart, movieSearchId, orderDetails} from "backend/idm";
import {useParams} from "react-router-dom";
import {useForm} from "react-hook-form";
import {FaBookmark, FaStar} from "react-icons/fa";


const OrderDetails = () =>{

    const {accessToken} = useUser();
    const [posting, setPosting] = useState([]);
    const [total, setTotal] = useState(0);
    const {saleId} = useParams();

    useEffect(() => {
        const payLoad = {
            saleId : saleId,
            accessToken : accessToken
        };

        orderDetails(payLoad)
            .then(response =>{
                if (response.data.result["code"] === 3090)
                    setPosting(response.data["items"]);
                    setTotal(response.data["total"]);
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }, []);

    return (
        <div>
        {posting && posting.map((post) => {
            return (<div key={post.movieId}>
                <h1>{post.movieTitle}</h1>
                <h1>{post.quantity}</h1>
                <h1>{post.unitPrice}</h1>
            </div>);
        })}
            <h1>Total: ${total}</h1>
        </div>
    )
}

export default OrderDetails;