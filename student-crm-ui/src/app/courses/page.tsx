import React from 'react';
import CourseCreator from "@/app/components/course/CourseCreator";
import CourseTable from "@/app/components/course/CourseTable";
import Dashboard from "@/app/components/common/Dashboard";
import Box from '@mui/material/Box';

const CoursesPage = () => {
    return (
        <Box sx={{
            display: 'flex',
            height: '100vh',
            width: '100%',
            overflowY: 'auto'
        }}>
            <Box sx={{width: '10%', bgcolor: 'background.paper'}}>
                <Dashboard/>
            </Box>
            <Box sx={{
                width: '90%',
                display: 'flex',
                justifyContent: 'center',
                bgcolor: 'background.default',
                p: 3
            }}>
                <CourseTable/>
                <CourseCreator/>
            </Box>
        </Box>
    );
};

export default CoursesPage;
