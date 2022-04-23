import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { User } from '../types';

const initialUser: User = {
    username: "",
    password: "",
    accessToken: ""
}

const initialUserLoginState = {
    user: initialUser,
    isLoggedIn: false
}

/**
 * Used for managing the React state.
 * It defines set og reducers that are called when the appropriate action is dispatched.
 */
export const userSlice = createSlice({
    name: 'user',
    initialState: initialUserLoginState,
    reducers: {
        login: (state, action: PayloadAction<User>) =>
        {
            state.user.username = action.payload.username;
            state.user.password = action.payload.password;
            state.user.accessToken = action.payload.accessToken;
            state.isLoggedIn = true;
        },
        logout: (state) =>
        {
            state.user = initialUser;
            state.isLoggedIn = false;
        }
    }
});

export const { login, logout } = userSlice.actions;

export default userSlice.reducer;