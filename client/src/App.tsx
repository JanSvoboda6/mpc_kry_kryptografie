import React, {Component, useEffect} from "react";
import {Router, Switch, Route, Link, Redirect} from "react-router-dom";
import "./App.css";
import Login from "./components/pages/LoginPage";
import Register from "./components/pages/RegisterPage";
import { history } from "./helpers/History";
import { User } from "./types";
import FileHandlerPage from "./components/pages/FileHandlerPage";
import PrivateRoute from "./helpers/PrivateRoute";
import Crypto from "./components/crypto/CryptoPage";

function App()
{
    const isUserLoggedIn = localStorage.getItem("user")
    return (
      <div>
        <style>
          @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
        </style>
        <Router history={history} >
            <Switch>
              <PrivateRoute exact path={["/"]} component={FileHandlerPage} />
              <Route exact path="/login">
                  {isUserLoggedIn? <Redirect to="/crypto"/> : <Login/>}
              </Route>
              <Route exact path="/register">
                  {isUserLoggedIn? <Redirect to="/crypto"/> : <Register/>}
              </Route>
              <Route exact path="/logout" component={Login} />
              <PrivateRoute exact path="/crypto" component={Crypto} />
              <PrivateRoute exact path="/files" component={FileHandlerPage} />
            </Switch>
        </Router>
      </div>
    );
}
export default App;