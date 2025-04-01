"use client"

import React, {useState, useEffect} from 'react';
import {useStudents} from "@/app/contexts/StudentContext";
import StudentEditor from "@/app/components/student/StudentEditor";
import {
    Box,
    TextField,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    Button,
    Tooltip,
    InputAdornment
} from '@mui/material';
import {useSnackbar} from "@/app/contexts/SnackbarContext";
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import InfoIcon from '@mui/icons-material/Info';
import SearchIcon from '@mui/icons-material/Search';

function StudentTable() {
    const {students, fetchStudents, updateStudent, deleteStudentById} = useStudents();
    const [searchTerm, setSearchTerm] = useState("");
    const [editingIndex, setEditingIndex] = useState(-1);
    const {showSnackbar} = useSnackbar()

    useEffect(() => {
        fetchStudents();
    }, []);

    const startEditing = (index: number) => {
        setEditingIndex(index);
    };

    const cancelEditing = () => {
        setEditingIndex(-1);
    };

    const saveChanges = async (studentId: number, firstName: string, lastName: string, email: string) => {
        await updateStudent(studentId, firstName, lastName, email);
        cancelEditing();
    };

    const handleDeleteStudent = (studentId: number) => {
        try {
            deleteStudentById(studentId);
            showSnackbar("Student successfully deleted", 'success')
        } catch (error: any) {
            showSnackbar(error.message, "error")
        }
    };

    const filteredStudents = students.filter(student =>
        student.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        student.lastName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        student.email.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <Box sx={{
            xs: 'block',
            md: 'flex',
            bgcolor: 'background.paper',
            p: 2,
            width: '100%',
            height: '100%',
            overflowY: 'auto'
        }}>
            <TextField
                type="text"
                placeholder="Search a student"
                onChange={e => setSearchTerm(e.target.value)}
                fullWidth
                variant="outlined"
                sx={{mb: 2}}
                InputProps={{
                    startAdornment: (
                        <InputAdornment position="start">
                            <SearchIcon sx={{color: "darkgray"}}/>
                        </InputAdornment>
                    )
                }}
            />
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Last Name</TableCell>
                        <TableCell>First Name</TableCell>
                        <TableCell>Email</TableCell>
                        <TableCell></TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {filteredStudents.map((student, index) => (
                        <TableRow key={student.id}>
                            <TableCell>
                                {editingIndex === index ? (
                                    <StudentEditor
                                        student={student}
                                        onCancel={cancelEditing}
                                        onSave={(firstName, lastName, email) =>
                                            saveChanges(student.id, firstName, lastName, email)}/>
                                ) : (
                                    student.lastName
                                )}
                            </TableCell>
                            <TableCell>
                                {editingIndex !== index && student.firstName}
                            </TableCell>
                            <TableCell>
                                {editingIndex !== index && student.email}
                            </TableCell>
                            <TableCell align="right">
                                {editingIndex !== index && (
                                    <>
                                        <Tooltip title="Edit">
                                            <Button onClick={() => startEditing(index)}>
                                                <EditIcon/>
                                            </Button>
                                        </Tooltip>
                                        <Tooltip title="Delete">
                                            <Button onClick={() => handleDeleteStudent(student.id)}>
                                                <DeleteIcon/>
                                            </Button>
                                        </Tooltip>
                                        <Tooltip title="View details">
                                            <Button onClick={() => location.href = `/students/${student.id}`}>
                                                <InfoIcon/>
                                            </Button>
                                        </Tooltip>
                                    </>
                                )}
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </Box>
    );
}

export default StudentTable;