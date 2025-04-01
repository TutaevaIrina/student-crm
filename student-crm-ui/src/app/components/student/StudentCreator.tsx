"use client"
import React, {useState} from "react";
import {useStudents} from "@/app/contexts/StudentContext";
import {Button, TextField, Box, InputAdornment, IconButton} from '@mui/material';
import {useSnackbar} from "@/app/contexts/SnackbarContext";
import {validateStudentFields} from "@/app/utils/apiUtils";
import ClearIcon from "@mui/icons-material/Clear";


function StudentCreator() {
    const [firstName, setFirstName] = useState<string>("");
    const [lastName, setLastName] = useState<string>("");
    const [email, setEmail] = useState<string>("");
    const [errors, setErrors] = useState<Record<string, string>>({})

    const {createStudent} = useStudents()
    const {showSnackbar} = useSnackbar()

    const handleAddStudent = async () => {
        const validationErrors = validateStudentFields({firstName, lastName, email});
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors)
            return;
        }
        try {
            await createStudent(firstName, lastName, email)
            setFirstName("");
            setLastName("");
            setEmail("");
            setErrors({});
            showSnackbar("Student successfully created", 'success');
        } catch (error: any) {
            showSnackbar(error.message, "error");
        }
    };

    const handleClearFirstName = () => {
        setFirstName("");
        setErrors(prevErrors => {
            const {firstName, ...rest} = prevErrors;
            return rest;
        });
    }

    const handleClearLastName = () => {
        setLastName("");
        setErrors(prevErrors => {
            const {lastName, ...rest} = prevErrors;
            return rest;
        });
    }

    const handleClearEmail = () => {
        setEmail("");
        setErrors(prevErrors => {
            const {email, ...rest} = prevErrors;
            return rest;
        });
    }

    return (
        <Box sx={{xs: 'block', md: 'flex', bgcolor: 'background.paper', p: 2, marginLeft: '20px', width: '40%'}}>
            <TextField
                type="text"
                label={errors.firstName ? "Error" : "First name"}
                name="firstName"
                value={firstName}
                onChange={event => setFirstName(event.target.value)}
                placeholder="First name"
                fullWidth
                variant="outlined"
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
                sx={{mb: 2, marginLeft: '10px'}}
            />
            <TextField
                type="text"
                label={errors.lastName ? "Error" : "Last name"}
                name="lastName"
                value={lastName}
                onChange={event => setLastName(event.target.value)}
                placeholder="Last name"
                fullWidth
                variant="outlined"
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
                sx={{mb: 2, marginLeft: '10px'}}
            />
            <TextField
                type="text"
                label={errors.email ? "Error" : "Email"}
                name="email"
                value={email}
                onChange={event => setEmail(event.target.value)}
                placeholder="e-mail"
                fullWidth
                variant="outlined"
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
                sx={{mb: 2, marginLeft: '10px'}}
            />
            <Button sx={{marginLeft: '10px'}} variant="contained" color="primary" onClick={handleAddStudent}>Create
                student</Button>
        </Box>
    );
}

export default StudentCreator