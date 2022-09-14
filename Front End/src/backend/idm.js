import Config from "backend/config.json";
import Axios from "axios";


/**
 * We use axios to create REST calls to our backend
 *
 * We have provided the login rest call for your
 * reference to build other rest calls with.
 *
 * This is an async function. Which means calling this function requires that
 * you "chain" it with a .then() function call.
 * <br>
 * What this means is when the function is called it will essentially do it "in
 * another thread" and when the action is done being executed it will do
 * whatever the logic in your ".then()" function you chained to it
 * @example
 * login(request)
 * .then(response => alert(JSON.stringify(response.data, null, 2)));
 */
export async function login(loginRequest) {
    const requestBody = {
        email: loginRequest.email,
        password: loginRequest.password
    };

    const options = {
        method: "POST", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.baseUrlForLogin, // Base URL (localhost:8081 for example)
        url: Config.idm.login, // Path of URL ("/login")
        data: requestBody // Data to send in Body (The RequestBody to send)
    }

    return Axios.request(options);
}

export async function register1(registerRequest) {
    const requestBody = {
        email: registerRequest.email,
        password: registerRequest.password
    };

    const options = {
        method: "POST", // Method type ("POST", "GET", "DELETE", ect)
        baseURL: Config.baseUrlForLogin, // Base URL (localhost:8081 for example)
        url: Config.idm.register, // Path of URL ("/register")
        data: requestBody // Data to send in Body (The RequestBody to send)
    }

    return Axios.request(options);
}


export async function movieSearch(movieSearchRequest)
{
    const requestBody = {
        title: movieSearchRequest.title,
        year: movieSearchRequest.year,
        director: movieSearchRequest.director,
        limit: movieSearchRequest.limit,
        page: movieSearchRequest.page,
        orderBy: movieSearchRequest.orderBy,
        genre: movieSearchRequest.genre,
        direction: movieSearchRequest.direction
    };

    //accessToken
    const accessToken = movieSearchRequest.accessToken;

    const options = {
        method : "GET",
        baseURL: Config.baseUrlForSearches,
        url: Config.movie.searchMovies,
        params: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function movieSearchId(movieSearchIdRequest)
{
    const requestBody = {
        movieId : movieSearchIdRequest.movieId
    };

    const accessToken = movieSearchIdRequest.accessToken;

    const options = {
        method : "GET",
        baseURL: Config.baseUrlForSearches,
        url: Config.movie.searchMovieById + movieSearchIdRequest.movieId,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function addToCart(addCartRequest)
{
    const requestBody = {
        movieId : addCartRequest.movieId,
        quantity : addCartRequest.quantity
    };

    const accessToken = addCartRequest.accessToken;

    const options = {
        method : "POST",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.cartInsert,
        data : requestBody,
        headers : {
            Authorization: "Bearer " + accessToken
        }
    };
    return Axios.request(options);
}

export async function updateCart(updateCartRequest)
{
    const requestBody = {
        movieId : updateCartRequest.movieId,
        quantity : updateCartRequest.quantity
    };

    const accessToken = updateCartRequest.accessToken;

    const options = {
        method : "POST",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.cartUpdate,
        data : requestBody,
        headers : {
            Authorization: "Bearer " + accessToken
        }
    };
    return Axios.request(options);
}

export async function cartRetrieve(cartRetrieveRequest)
{
    const accessToken = cartRetrieveRequest.accessToken;

    const options = {
        method : "GET",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.cartRetrieve,
        headers : {
            Authorization : "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function cartMovieDelete(cartMovieDeleteRequest)
{
    const accessToken = cartMovieDeleteRequest.accessToken;

    const options = {
        method : "DELETE",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.cartDelete + cartMovieDeleteRequest.movieId,
        headers : {
            Authorization : "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function cartClear(cartClearRequest)
{
    const accessToken = cartClearRequest.accessToken;

    const options = {
        method : "POST",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.cartClear,
        headers : {
            Authorization : "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function checkOut(checkOutRequest)
{
    const accessToken = checkOutRequest.accessToken;

    const options = {
        method : "GET",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.checkOut,
        headers : {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function orderComplete(orderCompleteRequest)
{
    const request = {
        paymentIntentId : orderCompleteRequest.paymentIntentId
    }
    const options = {
        method : "POST",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.orderComplete,
        headers : {
            Authorization : "Bearer " + orderCompleteRequest.accessToken
        },
        data : request
    };

    return Axios.request(options);
}

export async function orderList(orderListRequest)
{
    const requestBody = {
        paymentIntentId : orderListRequest.paymentIntentId
    }
    const options = {
        method : "GET",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.orderList,
        data : requestBody,
        headers : {
            Authorization : "Bearer " + orderListRequest.accessToken
        }
    };

    return Axios.request(options);
}

export async function orderDetails(orderDetailRequest)
{
    const saleId = orderDetailRequest.saleId;

    const options = {
        method : "GET",
        baseURL : Config.baseUrlForBilling,
        url : Config.billing.orderDetail + saleId,
        headers : {
            Authorization : "Bearer " + orderDetailRequest.accessToken
        }
    };
    return Axios.request(options);
}