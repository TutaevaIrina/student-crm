import React, {useEffect, useState} from 'react';
import {useCourses} from "@/app/contexts/CourseContext";
import {Button, TextField, Box, Alert, Snackbar, InputAdornment, IconButton} from '@mui/material';
import {useSnackbar} from "@/app/contexts/SnackbarContext";
import {validateCourseFields} from "@/app/utils/apiUtils";
import ClearIcon from "@mui/icons-material/Clear";

interface CourseEditorProps {
    course: {
        id: number;
        courseName: string;
    };
    onCancel: () => void;
    onSave: (courseName: string) => void;
}

function CourseEditor({course, onCancel, onSave}: CourseEditorProps) {
    const [editedName, setEditedName] = useState<string>(course.courseName);
    const {updateCourse} = useCourses();
    const {showSnackbar} = useSnackbar()
    const [errors, setErrors] = useState<Record<string, string>>({})
    const handleSave = async () => {
        const validationErrors = validateCourseFields({courseName: editedName});
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors)
            return;
        }
        try {
            await updateCourse(course.id, editedName);
            onSave(editedName);
            showSnackbar("Course successfully updated", "success");
        } catch (error: any) {
            showSnackbar(error.message, "error")
        }
    }

    const handleClearCourseName = () => {
        setEditedName("");
        setErrors({});
    }

    return (
        <Box sx={{display: 'flex', flexDirection: 'column', gap: 2}}>
            <TextField
                variant="outlined"
                label={errors.courseName ? "Error" :"Edit Course Name"}
                value={editedName}
                onChange={e => setEditedName(e.target.value)}
                fullWidth
                error={!!errors.courseName}
                helperText={errors.courseName}
                InputProps={{
                    endAdornment: (
                        <InputAdornment position="end">
                            <IconButton onClick={handleClearCourseName} sx={{color: "text.secondary"}}>
                                <ClearIcon/>
                            </IconButton>
                        </InputAdornment>
                    )
                }}
            />
            <Box>
                <Button variant="contained" color="primary" onClick={handleSave} sx={{marginRight: 1}}>
                    Save
                </Button>
                <Button variant="contained" color="secondary" onClick={onCancel}>
                    Cancel
                </Button>
            </Box>
        </Box>
    );
}

export default CourseEditor;