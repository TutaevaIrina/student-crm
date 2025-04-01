import {Student} from "@/app/types/Student";
import {useStudents} from "@/app/contexts/StudentContext";
import React, {useEffect, useState} from "react";
import {Button, TextField, Box, Alert, Snackbar, InputAdornment, IconButton} from '@mui/material';
import {useSnackbar} from "@/app/contexts/SnackbarContext";
import {validateCourseFields, validateStudentFields} from "@/app/utils/apiUtils";
import ClearIcon from "@mui/icons-material/Clear";


interface StudentEditorProps {
    student: Student;
    onCancel: () => void;
    onSave: (firstName: string, lastName: string, email: string) => void
}

function StudentEditor({student, onCancel, onSave}: StudentEditorProps) {
    const {updateStudent} = useStudents();
    const [editedFirstName, setEditedFirstName] = useState(student.firstName);
    const [editedLastName, setEditedLastName] = useState(student.lastName);
    const [editedEmail, setEditedEmail] = useState(student.email);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const {showSnackbar} = useSnackbar()

    const handleSave = async () => {
        const validationErrors = validateStudentFields({firstName: editedFirstName, lastName: editedLastName, email: editedEmail});
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors)
            return;
        }
        try {
            await updateStudent(student.id, editedFirstName, editedLastName, editedEmail);
            onSave(editedFirstName, editedLastName, editedEmail);
            showSnackbar("Student successfully updated", 'success')
        } catch (error: any) {
            showSnackbar(error.message, "error")
        }
    }

    const handleClearFirstName = () => {
        setEditedFirstName("");
        setErrors(prevErrors => {
            const {firstName, ...rest} = prevErrors;
            return rest;
        });
    }

    const handleClearLastName = () => {
        setEditedLastName("");
        setErrors(prevErrors => {
            const {lastName, ...rest} = prevErrors;
            return rest;
        });
    }

    const handleClearEmail = () => {
        setEditedEmail("");
        setErrors(prevErrors => {
            const {email, ...rest} = prevErrors;
            return rest;
        });
    }

    return (
        <Box sx={{display: 'flex', flexDirection: 'column', gap: 2}}>
            <TextField
                variant="outlined"
                label={errors.firstName ? "Error" : "Edit first name"}
                type="text"
                value={editedFirstName}
                onChange={e => setEditedFirstName(e.target.value)}
                error={!!errors.firstName}
                helperText={errors.firstName}
                InputProps={{
                    endAdornment: (
                        <InputAdornment position="end">
                            <IconButton onClick={handleClearFirstName} sx={{color: "text.secondary"}}>
                                <ClearIcon/>
                            </IconButton>
                        </InputAdornment>
                    )
                }}
            />
            <TextField
                variant="outlined"
                label={errors.lastName ? "Error" : "Edit last name"}
                type="text"
                value={editedLastName}
                onChange={e => setEditedLastName(e.target.value)}
                error={!!errors.lastName}
                helperText={errors.lastName}
                InputProps={{
                    endAdornment: (
                        <InputAdornment position="end">
                            <IconButton onClick={handleClearLastName} sx={{color: "text.secondary"}}>
                                <ClearIcon/>
                            </IconButton>
                        </InputAdornment>
                    )
                }}
            />
            <TextField
                variant="outlined"
                label={errors.email ? "Error" : "Edit email"}
                type="text"
                value={editedEmail}
                onChange={e => setEditedEmail(e.target.value)}
                error={!!errors.email}
                helperText={errors.email}
                InputProps={{
                    endAdornment: (
                        <InputAdornment position="end">
                            <IconButton onClick={handleClearEmail} sx={{color: "text.secondary"}}>
                                <ClearIcon/>
                            </IconButton>
                        </InputAdornment>
                    )
                }}
            />
            <Box>
                <Button variant="contained" color="primary" onClick={handleSave} sx={{marginRight: 1}}>Save</Button>
                <Button variant="contained" color="secondary" onClick={onCancel}>Cancel</Button>
            </Box>
        </Box>
    )
}

export default StudentEditor