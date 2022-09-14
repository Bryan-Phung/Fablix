import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {useForm} from "react-hook-form";
import {login, register} from "backend/idm";
import { useNavigate } from "react-router-dom";
import Form from "react-bootstrap/Form";

const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const StyledDivTextbox = styled.div`
    padding-top : 10px;
    position: grid;
    grid-template-columns: auto auto;
`

const StyledH1 = styled.h1`
    display: flex;
    color: white;
    margin: 0;
    padding: 0;
`

const StyledInput = styled.input`
    height: 80px;
    width: 400px;
    background-color: white;
    color : #002147;
    font-size : 20px;
    border-radius: 10px;
    margin: 0;
    padding: 0;
`

const StyledButton = styled.button`
    background-color: white;
    color : #002147;
    width: 200px;
    height : 50px;
    font-size: 20px;
    border-radius: 10px;
`

const StyledNotAUser = styled.button`
    color : white;
    width: 200px;
    height : 50px;
    font-size: 20px;
    border-radius: 10px;
    padding-left : 15px;
`
/**
 * useUser():
 * <br>
 * This is a hook we will use to keep track of our accessToken and
 * refreshToken given to use when the user calls "login".
 * <br>
 * For now, it is not being used, but we recommend setting the two tokens
 * here to the tokens you get when the user completes the login call (once
 * you are in the .then() function after calling login)
 * <br>
 * These have logic inside them to make sure the accessToken and
 * refreshToken are saved into the local storage of the web browser
 * allowing you to keep values alive even when the user leaves the website
 * <br>
 * <br>
 * useForm()
 * <br>
 * This is a library that helps us with gathering input values from our
 * users.
 * <br>
 * Whenever we make a html component that takes a value (<input>, <select>,
 * ect) we call this function in this way:
 * <pre>
 *     {...register("email")}
 * </pre>
 * Notice that we have "{}" with a function call that has "..." before it.
 * This is just a way to take all the stuff that is returned by register
 * and <i>distribute</i> it as attributes for our components. Do not worry
 * too much about the specifics of it, if you would like you can read up
 * more about it on "react-hook-form"'s documentation:
 * <br>
 * <a href="https://react-hook-form.com/">React Hook Form</a>.
 * <br>
 * Their documentation is very detailed and goes into all of these functions
 * with great examples. But to keep things simple: Whenever we have a html with
 * input we will use that function with the name associated with that input,
 * and when we want to get the value in that input we call:
 * <pre>
 * getValue("email")
 * </pre>
 * <br>
 * To Execute some function when the user asks we use:
 * <pre>
 *     handleSubmit(ourFunctionToExecute)
 * </pre>
 * This wraps our function and does some "pre-checks" before (This is useful if
 * you want to do some input validation, more of that in their documentation)
 */
const Login = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();
    const navigate = useNavigate();
    const {register, getValues, handleSubmit} = useForm();

    const submitLogin = () => {
        const email = getValues("email");
        const password = getValues("password");

        const payLoad = {
            email: email,
            password: password.split('') //treat password as char[]
        }

        // console.log("Hello world")
        login(payLoad)
            .then(response => {
                setRefreshToken(response.data["refreshToken"]);
                setAccessToken(response.data["accessToken"]);
                navigate("/");
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2))); //correctly prints error
    }

    const goToRegister = () => {
        navigate("/register");
    }

    return (
        <StyledDiv>

            <StyledDiv>
                <StyledH1>UserName</StyledH1>
                <StyledInput {...register("email")} type={"email"}/>
            </StyledDiv>
            <StyledDiv>
                <StyledH1>Password</StyledH1>
                <StyledInput {...register("password")} type={"password"}/>
            </StyledDiv>
            <StyledDivTextbox>
                <StyledButton onClick={handleSubmit(submitLogin)}><b>Login</b></StyledButton>
                <StyledNotAUser onClick={handleSubmit(goToRegister)}><b>Not A User?</b></StyledNotAUser>
            </StyledDivTextbox>
        </StyledDiv>

    );
}

export default Login;
