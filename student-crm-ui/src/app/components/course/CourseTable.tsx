"use client"

import React, {useState, useEffect} from 'react';
import {useCourses} from "@/app/contexts/CourseContext";
import CourseEditor from "@/app/components/course/CourseEditor";
import {
    Box,
    TextField,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    Button,
    DialogTitle,
    Dialog,
    DialogContent,
    DialogContentText,
    DialogActions,
    Tooltip, InputAdornment,
} from '@mui/material';
import {useSnackbar} from "@/app/contexts/SnackbarContext";
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import InfoIcon from '@mui/icons-material/Info';
import GroupsIcon from '@mui/icons-material/Groups';
import SearchIcon from '@mui/icons-material/Search';

function CourseTable() {
    const {courses, fetchCourses, deleteCoursesById, updateCourse} = useCourses();
    const [searchTerm, setSearchTerm] = useState("");
    const [editingIndex, setEditingIndex] = useState(-1);
    const [open, setOpen] = useState(false);
    const [courseToDelete, setCourseToDelete] = useState<number | null>(null);
    const {showSnackbar} = useSnackbar()

    useEffect(() => {
        fetchCourses();
    }, []);

    const startEditing = (index: number) => {
        setEditingIndex(index);
    };

    const stopEditing = () => {
        setEditingIndex(-1);
    };

    const handleSave = (courseId: number, newName: string) => {
        updateCourse(courseId, newName);
        fetchCourses();
        stopEditing();
    };

    const handleClickOpen = (courseId: number) => {
        setCourseToDelete(courseId);
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setCourseToDelete(null);
    };

    const handleDeleteCourse = async () => {
        if (courseToDelete !== null) {
            try {
                await deleteCoursesById(courseToDelete);
                setOpen(false);
                showSnackbar("Course successfully deleted", 'success');
            } catch (error: any) {
                setOpen(false);
                showSnackbar(error.message || "An unexpected error occurred", "error");
            }
        }
    }

    return (
        <Box sx={{
            xs: 'block',
            md: 'flex',
            bgcolor: 'background.paper',
            p: 2,
            width: '80%',
            height: '100%',
            overflowY: 'auto'
        }}>
            <TextField
                type="text"
                placeholder="Search a course"
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

                <TableBody>
                    {courses.filter(course => course.courseName.toLowerCase().includes(searchTerm.toLowerCase())).map((course, index) => (
                        <TableRow key={course.id}>
                            <TableCell>
                                {editingIndex === index ? (
                                    <CourseEditor
                                        course={course}
                                        onCancel={() => stopEditing()}
                                        onSave={(newName) => handleSave(course.id, newName)}
                                    />
                                ) : (
                                    course.courseName
                                )}
                            </TableCell>
                            <TableCell align="right"> {course.students && course.students.length > 0 && (
                                <Tooltip title={`${course.students.length} student(s) enrolled in the course`}>
                                    <GroupsIcon/>
                                </Tooltip>
                            )}
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
                                            <Button onClick={() => handleClickOpen(course.id)}>
                                                <DeleteIcon/>
                                            </Button>
                                        </Tooltip>
                                        <Tooltip title="View details">
                                            <Button
                                                onClick={() => window.location.href = `/courses/${course.id}`}>
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
            <Dialog
                open={open}
                onClose={handleClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">
                    {"Delete course"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Are you sure you want to delete this course?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Nein
                    </Button>
                    <Button onClick={handleDeleteCourse} color="primary" autoFocus>
                        Ja
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}
export default CourseTable;

