import React from 'react';
import { Route, Redirect } from 'react-router-dom';

const PrivateRoute = ({component: Component, ...rest}) => {
    return (
        <Route {...rest} render={props => (
            localStorage.getItem('user') && localStorage.getItem('crypto_key') ? <Component {...props} /> : <Redirect to="/login"/>
        )} />
    );
};

export default PrivateRoute;