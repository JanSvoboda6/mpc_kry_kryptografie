import { configureStore } from '@reduxjs/toolkit'
import userReducer from './UserSlice'

import thunk from "redux-thunk";

/**
 * Defines store for React state management. The state is managed by redux.
 */
export const store = configureStore({
    reducer: {
        user: userReducer
    },
    middleware: [thunk]
})