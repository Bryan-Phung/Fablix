import React from "react";
import {NavLink} from "react-router-dom";
import styled from "styled-components";
import {useUser} from "../hook/User";
import {FaSearch, FaCartPlus,FaHistory} from 'react-icons/fa';
// import {RiFileHistoryFill} from 'react-icons/Ri';

const StyledNav = styled.nav`
  display: flex;
  justify-content: center;
  background-color: --light-azure;
  height: 70px;
`;

const leftNav = styled.div`
  margin-left: 10px;
  background-color: --light-azure;
`;

const rightNav = styled.ul`
  display: flex;
`;

const StyledNavLink = styled(NavLink)`
  height: 70px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: --light-azure;
  margin-top: 0px;
`;

const StyledNavLinkRight = styled(NavLink)`
  height: 70px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: --light-azure;
`;

const StyledImg = styled.img`
    background-color: --light-azure;
    margin-right: 60px;
`;

/**
 * To be able to navigate around the website we have these NavLink's (Notice
 * that they are "styled" NavLink's that are now named StyledNavLink)
 * <br>
 * Whenever you add a NavLink here make sure to add a corresponding Route in
 * the Content Component
 * <br>
 * You can add as many Link as you would like here to allow for better navigation
 * <br>
 * Below we have two Links:
 * <li>Home - A link that will change the url of the page to "/"
 * <li>Login - A link that will change the url of the page to "/login"
 */
const NavBar = () => {

    const {accessToken} = useUser();

    return (
        <StyledNav>

                {/*navigation bar*/}
            <ul className="navbar-nav">
                <StyledNavLink to="/">
                    <StyledImg src="https://pbs.twimg.com/media/Ek8rZRPXYAEkSN9.jpg"
                               alt="Logo Screen"/>

                </StyledNavLink>
                <StyledNavLink to="/">
                    <li>Home</li>
                </StyledNavLink>

                <StyledNavLink to="/androidApp">
                    <li>Android App</li>
                </StyledNavLink>
            </ul>

            <ul className="navbar-navRight">
                <StyledNavLink to ="/movies/search">
                    <FaSearch/>
                </StyledNavLink>

                {!accessToken &&
                    <StyledNavLink to="/login">
                        <li>Login</li>
                    </StyledNavLink>
                }
                {!accessToken && <StyledNavLink to="/register">
                    <li>Register</li>
                </StyledNavLink>}
                {accessToken && <StyledNavLink to="/cart/retrieve">
                    <li><FaCartPlus/></li>
                </StyledNavLink>}
                {accessToken && <StyledNavLink to = "/order/history">
                    <li><FaHistory/></li>
                    </StyledNavLink>

                }
            </ul>

        </StyledNav>
    );
}

export default NavBar;
