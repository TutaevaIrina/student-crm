"use client"

import {useStudents} from "@/app/contexts/StudentContext";
import {useCallback, useEffect, useState} from "react";
import {useCourses} from "@/app/contexts/CourseContext";
import {StudentSimple} from "@/app/types/StudentSimple";
import {
    Box,
    Typography,
    List,
    ListItem,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Snackbar,
    Alert,
    ListItemText
} from '@mui/material';
import {useSnackbar} from "@/app/contexts/SnackbarContext";

interface AddStudentProps {
    courseId: number;
}

function AddRemoveStudent({courseId}: AddStudentProps) {
    const {students, fetchStudents} = useStudents();
    const {fetchCourseById, addStudentToCourse, removeStudentFromCourse} = useCourses();
    const [courseStudents, setCourseStudents] = useState<StudentSimple[]>([]);
    const [availableStudents, setAvailableStudents] = useState<StudentSimple[]>([]);
    const [selectedStudentId, setSelectedStudentId] = useState<number>(0);
    const {showSnackbar} = useSnackbar()


    useEffect(() => {
        fetchStudents();
        fetchCourseStudents();
    }, [courseId]);

    const fetchCourseStudents = useCallback(async () => {
        const course = await fetchCourseById(courseId);
        setCourseStudents(course.students);

    }, [courseId, fetchCourseById]);

    useEffect(() => {
        const addedStudents = courseStudents.map(student => student.id);
        setAvailableStudents(students.filter(student => !addedStudents.includes(student.id)));
    }, [students, courseStudents]);


    const handleAddStudent = async () => {
        try {
            if (selectedStudentId) {
                await addStudentToCourse(courseId, selectedStudentId);
                await fetchCourseStudents();
                showSnackbar('Student added to the course', 'success');
                setSelectedStudentId(0);
            }
        } catch (error: any) {
            showSnackbar(error.message, "error")
        }
    };

    const handleRemoveStudent = async (studentId: number) => {
        try {
            await removeStudentFromCourse(courseId, studentId);
            await fetchCourseStudents();
            showSnackbar('Student removed from the course', 'success');
        } catch (error: any) {
            showSnackbar(error.message, "error");
        }
    };

    if (!students.length) {
        return <Typography>Loading...</Typography>;
    }

    return (
        <Box sx={{display: 'flex', gap: 3, pt: 3, color: 'text.primary', height: '100%'}}>
            <Box sx={{width: '70%', bgcolor: 'background.paper', p: 4, overflowY: 'auto'}}>
                <Typography variant="h5" gutterBottom borderBottom={1}>
                    Enrolled Students
                </Typography>
                {courseStudents.length === 0 ? (
                    <Typography>
                        No students enrolled in this course
                    </Typography>
                ) : (
                    <List>
                        {courseStudents.map(student => (
                            <ListItem key={student.id} sx={{display: 'flex', justifyContent: 'space-between'}}>
                                {student.lastName} {student.firstName}
                                <Button onClick={() => handleRemoveStudent(student.id)}>Remove</Button>
                            </ListItem>
                        ))}
                    </List>
                )}
            </Box>
            <Box sx={{width: '30%', bgcolor: 'background.paper', p: 4}}>
                <Typography variant="h6" gutterBottom>
                    Add Student to the course
                </Typography>
                <FormControl fullWidth sx={{mt: 1}}>
                    <InputLabel id="student-select-label">Select Student</InputLabel>
                    <Select
                        labelId="student-select-label"
                        value={selectedStudentId.toString()}
                        label="Select Student"
                        onChange={e => setSelectedStudentId(parseInt(e.target.value))}
                        MenuProps={{
                            PaperProps: {
                                style: {
                                    maxHeight: 224
                                }
                            }
                        }}
                    >
                        {availableStudents.length > 0 ? availableStudents.map(student => (
                            <MenuItem key={student.id} value={student.id.toString()}>
                                {student.lastName} {student.firstName}
                            </MenuItem>
                        )) : <MenuItem disabled>No students available to add</MenuItem>}
                    </Select>
                </FormControl>
                <Button variant="contained" color="primary" sx={{mt: 2}} onClick={handleAddStudent}>
                    Add
                </Button>
            </Box>
        </Box>
    );
}

export default AddRemoveStudent