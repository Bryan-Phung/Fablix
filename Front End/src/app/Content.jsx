import React from "react";
import {Route, Routes} from "react-router-dom";

import Login from "pages/Login";
import Home from "pages/Home";
import styled from "styled-components";
import Register from "../pages/Register";
import MovieSearch from "../pages/MovieSearch";
import MovieDetailWithId from "../pages/MovieDetailWithId";
import {useUser} from "../hook/User";
import CartDetail from "../pages/CartDetail";
import OrderPayment from "../pages/OrderPayment";
import OrderHistory from "../pages/OrderHistory";
import OrderComplete from "../pages/OrderComplete";
import OrderDetails from "../pages/OrderDetails";

const StyledDiv = styled.div`
  display: flex;
  justify-content: center;

  width: 100vw;
  height: 100vh;
  padding: 25px;

  background: --light-azure;
  box-shadow: inset 0 3px 5px -3px #000000;
`

/**
 * This is the Component that will switch out what Component is being shown
 * depending on the "url" of the page
 * <br>
 * You'll notice that we have a <Routes> Component and inside it, we have
 * multiple <Route> components. Each <Route> maps a specific "url" to show a
 * specific Component.
 * <br>
 * Whenever you add a Route here make sure to add a corresponding NavLink in
 * the NavBar Component.
 * <br>
 * You can essentially think of this as a switch statement:
 * @example
 * switch (url) {
 *     case "/login":
 *         return <Login/>;
 *     case "/":
 *         return <Home/>;
 * }
 *
 */
const Content = () => {

    const {accessToken} = useUser();

    return (
        <StyledDiv >
            <Routes>

                {!accessToken && <Route path="/login" element={<Login/>}/>}
                {!accessToken && <Route path="/register" element = {<Register/>}/>}
                <Route path="/movies/search" element = {<MovieSearch/>}/>
                <Route path="/" element={<Home/>}/>
                <Route path="/movie/:id" element={<MovieDetailWithId/>}/>
                <Route path="/cart/retrieve" element={<CartDetail/>}/>
                <Route path="/order/payment" element={<OrderPayment/>}/>
                <Route path="/order/list" element={<OrderHistory/>}/>
                <Route path="/order/complete" element={<OrderComplete/>}/>
                <Route path="/order/history" element={<OrderHistory/>}/>
                <Route path="/order/detail/:saleId" element={<OrderDetails/>}/>
            </Routes>
        </StyledDiv>
    );
}

export default Content;
