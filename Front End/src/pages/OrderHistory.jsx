import React, { useState, useEffect } from "react";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";
import {checkOut, movieSearchId, orderList} from "../backend/idm";
import {useUser} from "../hook/User";
import CheckoutForm from "./CheckoutForm";
import {useForm} from "react-hook-form";
import {useNavigate, useParams} from "react-router-dom";


const OrderHistory = () => {

    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const [sales, setSales] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = () => {
        const payLoad = {
            accessToken: accessToken
        };

        orderList(payLoad)
            .then(response => {
                // alert(JSON.stringify(response.data, null, 2));
                if (response.data.result["code"] === 3080)
                {
                    setSales(response.data["sales"]);
                }
                else
                    setSales([]);
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    };

    return (
        <div>
            <h1>Sales</h1>
            <div className="container-history">
            {sales && sales.map((sale) => {
                    return (<div className="sale-info" key = {sale.saleId}>
                    <h1>{sale.saleId}</h1>
                    <h1>{sale.orderDate}</h1>
                    <h1>Total: ${sale.total}</h1>
                    <a href={"/order/detail/" + sale.saleId}>
                    <h1><button>View Order Details</button></h1>
                    </a>
                    </div>)
                })
            }
            </div>
        </div>
    )

}

export default OrderHistory;