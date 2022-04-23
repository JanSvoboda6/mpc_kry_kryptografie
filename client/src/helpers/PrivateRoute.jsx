import React, {useEffect} from 'react';
import { Route, Redirect } from 'react-router-dom';
import jwtDecode from "jwt-decode";
import {useDispatch} from "react-redux";
import LogoutService from "../services/LogoutService";

const PrivateRoute = ({component: Component, ...rest}) => {

    const dispatch = useDispatch();

    useEffect(() => {
        logoutUserIfAccessTokenIsExpired();
    }, []);

    const logoutUserIfAccessTokenIsExpired = () => {
        const user = JSON.parse(localStorage.getItem("user"));
        if (user)
        {
            console.log(user);
            const {exp} = jwtDecode(user.accessToken);
            console.log(exp);
            if (Date.now() > exp * 1000)
            {
                LogoutService(dispatch);
            }
        }
    }

    const isUserLoggedInAndCryptoKeyIsNull = () => {
        return localStorage.getItem("user") && !localStorage.getItem("crypto_key");
    }

    return (
        <Route {...rest} render={props => (
            isUserLoggedInAndCryptoKeyIsNull() && window.location.pathname === "/files"? <Redirect to="/crypto"/> :
            localStorage.getItem('user') ? <Component {...props} /> : <Redirect to="/login"/>
        )} />
    );
};

export default PrivateRoute;