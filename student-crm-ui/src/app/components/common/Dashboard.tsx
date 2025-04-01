import React from 'react';
import { Button, Box } from '@mui/material';

const Dashboard = () => {
    return (
        <Box sx={{
            xs: 'block',
            md: 'flex',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'left',
            justifyContent: 'center',
            height: '100vh',
            bgcolor: 'background.default',
            color: 'text.primary'
        }}>
            <Button variant="text" color="primary" href="/courses" sx={{ margin: 1 }}>
                Courses
            </Button>
            <Button variant="text" color="primary" href="/students" sx={{ margin: 1 }}>
                Students
            </Button>
        </Box>
    );
};

export default Dashboard;