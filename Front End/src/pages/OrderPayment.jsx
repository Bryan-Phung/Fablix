import React, { useState, useEffect } from "react";
import { loadStripe } from "@stripe/stripe-js";
import { Elements } from "@stripe/react-stripe-js";
import {checkOut, movieSearchId} from "../backend/idm";
import {useUser} from "../hook/User";
import CheckoutForm from "./CheckoutForm";

const stripePromise = loadStripe("pk_test_51KxdfeI3cGv5tOVj12IosE6jYArTFjeIGyGpgnx5hBICGajBdBMTzOFyMHkVMqlcRL9rOr3nXnUIzk4HEgXgVxBc00Ko0QE3vV");

const OrderPayment = () => {

    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();
    const [clientSecret, setClientSecret] = useState("");


    useEffect(() => {
        submitOrderPayment();
    }, []);

    const submitOrderPayment = () => {
        const payLoad = {
            accessToken: accessToken
        };

        checkOut(payLoad)
            .then(response => {
                // alert(JSON.stringify(response.data, null, 2));
                if (response.data.result["code"] === 3060)
                {
                    setClientSecret(response.data["clientSecret"]);
                }
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    };

    const appearance = {
        theme: "stripe",
    };
    const options = {
        clientSecret,
        appearance,
    };

    return (
        <div className="OrderPayment">
            {clientSecret && (
                <Elements options={options} stripe={stripePromise}>
                    <CheckoutForm />
                </Elements>
            )}
        </div>
    );

}

export default OrderPayment;