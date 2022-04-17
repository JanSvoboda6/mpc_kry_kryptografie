import axios, { AxiosResponse } from "axios";
import { User } from "../types";
import {API_URL} from "../helpers/BackendApi";

class AuthenticationService
{
  async login(username: string, password: string) 
  {
    const response: AxiosResponse<User> = await axios.post(API_URL + "/api/auth/login", { username, password });

    if (response.data.accessToken)
    {
      localStorage.setItem("user", JSON.stringify(response.data));
    }
    return response.data;
  }

  logout()
  {
    localStorage.removeItem("user");
    localStorage.removeItem("key");
  }

  register(username: string, password: string)
  {
    var message: string = "";
    return axios.post(API_URL + "/api/auth/register", { username, password, message })
  }
}

export default new AuthenticationService();
