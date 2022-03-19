import axios, { AxiosResponse } from "axios";
import { User } from "../types";
import aesjs from "aes-js";

const API_URL = "http://localhost:8080/api/auth";

function generateKey() {
  const bytes = new Uint8Array(32);
  window.crypto.getRandomValues(bytes);
  console.log(aesjs.utils.hex.fromBytes(bytes));
  return aesjs.utils.hex.fromBytes(bytes);
}

class AuthenticationService
{
  async login(username: string, password: string) 
  {
    const response: AxiosResponse<User> = await axios.post(API_URL + "/login", { username, password });

    if (response.data.accessToken)
    {
      localStorage.setItem("user", JSON.stringify(response.data));
      localStorage.setItem("crypto_key", generateKey());
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
    return axios.post(API_URL + "/register", { username, password, message })
  }
}

export default new AuthenticationService();
