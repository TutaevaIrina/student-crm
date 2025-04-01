import React from 'react';
import StudentCreator from "@/app/components/student/StudentCreator";
import StudentTable from "@/app/components/student/StudentTable";
import Box from '@mui/material/Box';
import Dashboard from "@/app/components/common/Dashboard";

const StudentPage = () => {
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

                <StudentTable/>
                <StudentCreator/>
            </Box>
        </Box>
    );
};

export default StudentPage;