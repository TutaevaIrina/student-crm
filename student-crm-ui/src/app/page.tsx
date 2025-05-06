"use client"
import { Box, Button, Typography } from '@mui/material';
import theme from "@/app/styles/theme";

export default function Home() {
  return (
    <Box sx={{
      bgcolor: theme.palette.background.default,
      height: "100vh",
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center'
    }}>
      <Typography variant="h3" component="h1" sx={{ mb: 4, color: theme.palette.text.primary }}>
        Student CRM
      </Typography>
      <Box sx={{
        display: 'flex',
        gap: 3
      }}>
        <Button variant="contained" color="primary" href="/courses">
          Courses
        </Button>
        <Button variant="contained" color="primary" href="/students">
          Students
        </Button>
      </Box>
    </Box>
  );
}
