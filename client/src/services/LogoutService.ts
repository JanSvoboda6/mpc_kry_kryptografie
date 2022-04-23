import { logout as doLogout } from '../redux/UserSlice';
import AuthenticationService from './AuthenticationService';

/**
 * Simple service providing the user logout and dispatching the appropriate action.
 */
const logout = (dispatch: any) =>
{
    AuthenticationService.logout();
    dispatch(doLogout());
}

export default {logout};