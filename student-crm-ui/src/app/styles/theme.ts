"use client"

import { createTheme } from '@mui/material/styles';

const theme = createTheme({
    palette: {
        primary: {
            main: '#8ad2dd',
        },
        secondary: {
            main: '#253543',
        },
        error: {
            main: '#b86164'
        },
        background: {
            default: '#0b212c',
            paper: '#04131f'
        },
        text: {
            primary: '#F5F6FA',
            secondary: '#8ad2dd'
        }
    },
    typography: {
        fontFamily: 'Roboto, Arial, sans-serif',
    }
});

export default theme;