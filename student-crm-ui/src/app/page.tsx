"use client"

import Link from "next/link";
import {Button, Typography, Box, Grid} from '@mui/material';
import theme from "@/app/styles/theme";
import Dashboard from "@/app/components/common/Dashboard";

export default function Home() {
    return (
        <Box sx={{
            bgcolor: theme.palette.background.default,
            height: "100vh",
            padding: 3,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center'
        }}>
            <Box sx={{
                bgcolor: theme.palette.background.paper,
                padding: 3,
                borderRadius: 2,
                boxShadow: 3,
                height: "80vh",
                width: '80%',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                textAlign: 'center'
            }}>
                <Typography variant="h3" component="h2" sx={{color: theme.palette.text.primary, padding: 4, marginTop: -10}}>
                    Student CRM
                </Typography>
                <Grid container spacing={2} justifyContent="center">
                    <Dashboard />

                </Grid>
            </Box>
        </Box>
    );
}
