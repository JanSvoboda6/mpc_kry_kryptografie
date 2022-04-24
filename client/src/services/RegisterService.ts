import AuthenticationService from "../services/AuthenticationService";

/**
 * Service for user registration.
 */
const register = (username: string, password: string): any =>
{
    return AuthenticationService.register(username, password);
};

export default {register};