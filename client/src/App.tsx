import React, { Component } from "react";
import { Router, Switch, Route, Link } from "react-router-dom";
import "./App.css";
import Login from "./components/pages/LoginPage";
import Register from "./components/pages/RegisterPage";
import { history } from "./helpers/History";
import { User } from "./types";
import DatasetPage from "./components/pages/DatasetPage";
import PrivateRoute from "./helpers/PrivateRoute";

interface AppProps
{
  dispatch: any,
  user: User
}

class App extends Component<AppProps, User>
{
  render()
  {
    return (
      <div>
        <style>
          @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
        </style>
        < Router history={history} >
          <div className="navigation-page" >
            <Switch>
              <Route exact path="/login" component={Login} />
              <Route exact path="/register" component={Register} />
              <Route exact path="/logout" component={Login} />
              <PrivateRoute exact path="/datasets" component={DatasetPage} />
            </Switch>
          </div>
        </Router>
        <footer>
          <p>made by jan</p>
        </footer>
      </div>
    );
  }
}
export default App;