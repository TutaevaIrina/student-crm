"use client"

import React, {useState} from 'react';
import {useCourses} from "@/app/contexts/CourseContext";
import {useSnackbar} from "@/app/contexts/SnackbarContext";
import {Button, TextField, Box, InputAdornment, IconButton} from '@mui/material';
import ClearIcon from '@mui/icons-material/Clear';
import {validateCourseFields} from "@/app/utils/apiUtils";

function CourseCreator() {
    const [courseName, setCourseName] = useState("");
    const [errors, setErrors] = useState<Record<string, string>>({})
    const {createCourse} = useCourses();
    const {showSnackbar} = useSnackbar();

    const handleAddCourse = async () => {
        const validationErrors = validateCourseFields({courseName});
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }
        try {
            await createCourse(courseName);
            setCourseName("");
            showSnackbar("Course successfully created", "success");
            setErrors({});
        } catch (error: any) {
            showSnackbar(error.message, "error");
        }
    };

    const handleClear = () => {
        setCourseName("");
        setErrors({});
    }

    return (
        <Box sx={{xs: 'block', md: 'flex', bgcolor: 'background.paper', p: 2, marginLeft: '20px'}}>

            <TextField
                label={errors.courseName ? "Error" : "Course name"}
                value={courseName}
                onChange={event => setCourseName(event.target.value)}
                fullWidth
                variant="outlined"
                error={!!errors.courseName}
                helperText={errors.courseName}
                InputProps={{
                    endAdornment: (
                        <InputAdornment position="end">
                            <IconButton onClick={handleClear} sx={{color: "text.secondary"}}>
                                <ClearIcon/>
                            </IconButton>
                        </InputAdornment>
                    )
                }}
                sx={{mb: 2, marginLeft: '10px'}}
            />

            <Button sx={{marginLeft: '10px'}} variant="contained" color="primary" onClick={handleAddCourse}>
                Create course
            </Button>
        </Box>
    );
}

export default CourseCreator;