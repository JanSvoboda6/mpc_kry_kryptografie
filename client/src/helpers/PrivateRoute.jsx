import React, {useEffect} from 'react';
import { Route, Redirect } from 'react-router-dom';
import jwtDecode from "jwt-decode";
import {useDispatch} from "react-redux";
import LogoutService from "../services/LogoutService";

/**
 * Component for redirecting based on the state of user login.
 * If route is a private one, the application will log out user if the JWT token is expired.
 */
const PrivateRoute = ({component: Component, ...rest}) => {

    const dispatch = useDispatch();

    useEffect(() => {
        logoutUserIfAccessTokenIsExpired();
    });

    const logoutUserIfAccessTokenIsExpired = () => {
        const user = JSON.parse(localStorage.getItem("user"));
        if (user)
        {
            const {exp} = jwtDecode(user.accessToken);
            if (Date.now() > exp * 1000)
            {
                LogoutService.logout(dispatch);
            }
        }
    }

    const isUserLoggedInAndCryptoKeyIsNull = () => {
        return localStorage.getItem("user") && !localStorage.getItem("crypto_key");
    }

    return (
        <Route {...rest} render={props => (
            isUserLoggedInAndCryptoKeyIsNull() && (window.location.pathname === "/files" || window.location.pathname === "/") ? <Redirect to="/crypto"/> :
            localStorage.getItem("user") ? <Component {...props} /> : <Redirect to="/login"/>
        )} />
    );
};

export default PrivateRoute;