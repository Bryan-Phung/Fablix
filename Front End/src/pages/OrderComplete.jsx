import {useEffect} from "react";
import {useUser} from "../hook/User";
import {orderComplete} from "../backend/idm";


const OrderComplete = () =>
{

    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const submitOrderComplete = (id) =>
    {
        const payLoad = {
            accessToken : accessToken,
            paymentIntentId : id
        };

        orderComplete(payLoad)
            .then(response =>{
                if (response.data.result["code"] === 3070)
                    console.log(response.data.result["code"]);
                else
                    console.log("Failed to finish");
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));
    }

    useEffect(() => {

        const paymentIntent = new URLSearchParams(window.location.search).get(
            "payment_intent"
        );

        console.log("paymentIntent: " + paymentIntent);
        submitOrderComplete(paymentIntent);
    }, []);

    return (
        <div>Order is Complete</div>
    )
}

export default OrderComplete;