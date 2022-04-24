import { AuthorizationHeader as AuthorizationHeaderInterface, User } from "../types";

/**
 * AuthoriyationHeader is user for adding the JWT token to the HTTP header.
 * On the server side, token is decrypted and user information are retrieved.
 * JWT token is used for accessing the resource on the backend server.
 */
export default function AuthorizationHeader()
{
    const user: User = JSON.parse(localStorage.getItem('user') || '{}');

    if (user && user.accessToken)
    {
        var authorizationHeader: AuthorizationHeaderInterface = { Authorization: 'Bearer ' + user.accessToken };
        return authorizationHeader;
    } else
    {
        return {};
    }
}