import {useStudents} from "@/app/contexts/StudentContext";
import {useCourses} from "@/app/contexts/CourseContext";
import {useCallback, useEffect, useState} from "react";
import {CourseSimple} from "@/app/types/CourseSimple";
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
    Alert
} from '@mui/material';
import {useSnackbar} from "@/app/contexts/SnackbarContext";

interface AddCourseProps {
    studentId: number;
}

function AddRemoveCourse({studentId}: AddCourseProps) {
    const {courses, fetchCourses, addStudentToCourse, removeStudentFromCourse} = useCourses();
    const {fetchStudentById} = useStudents();
    const [studentCourses, setStudentCourses] = useState<CourseSimple[]>([]);
    const [availableCourses, setAvailableCourses] = useState<CourseSimple[]>([]);
    const [selectedCourseId, setSelectedCourseId] = useState<number>(0);
    const {showSnackbar} = useSnackbar()


    const fetchStudentCourses = useCallback(async () => {
        const student = await fetchStudentById(studentId);
        setStudentCourses(student.courses)
    }, [studentId, fetchStudentById]);

    useEffect(() => {
        const fetchData = async () => {
            await fetchCourses();
            await fetchStudentCourses();
        }
        fetchData();
    }, [fetchCourses, fetchStudentCourses]);


    useEffect(() => {
        const studentCourseIds = new Set(studentCourses.map(course => course.id));
        setAvailableCourses(courses.filter(course => !studentCourseIds.has(course.id)));
    }, [courses, studentCourses]);

    const handleAddCourse = async () => {
        try {
            if (selectedCourseId) {
                await addStudentToCourse(selectedCourseId, studentId);
                await fetchStudentCourses();
                setSelectedCourseId(0)
                showSnackbar("Student added to the course", 'success');
            }
        } catch (error: any) {
            showSnackbar(error.message, "error")
        }
    };

    const handleRemoveCourse = async (courseId: number) => {
        try {
            await removeStudentFromCourse(courseId, studentId);
            const student = await fetchStudentById(studentId);
            setSelectedCourseId(0)
            setStudentCourses(student.courses)
            showSnackbar("Student removed from the course", 'success');
        } catch (error: any) {
            showSnackbar(error.message, "error")
        }
    };

    return (
        <Box sx={{display: 'flex', gap: 3, pt: 3, color: 'text.primary', height: '100%', overflowY: 'auto'}}>
            <Box sx={{width: '70%', bgcolor: 'background.paper', p: 4, overflowY: 'auto'}}>
                <Typography variant="h5" gutterBottom borderBottom={1}>
                    Current Courses
                </Typography>
                {studentCourses.length === 0 ? (
                    <Typography>
                        The student does not enrolled in any course
                    </Typography>
                ) : (<List>
                        {studentCourses.map(course => (
                            <ListItem key={course.id} sx={{display: 'flex', justifyContent: 'space-between'}}>
                                {course.courseName}
                                <Button onClick={() => handleRemoveCourse(course.id)}>Remove</Button>
                            </ListItem>
                        ))}
                    </List>
                )}
            </Box>
            <Box sx={{width: '30%', bgcolor: 'background.paper', p: 4}}>
                <Typography variant="h6" gutterBottom>
                    Add to Course
                </Typography>
                <FormControl fullWidth sx={{mt: 1}}>
                    <InputLabel id="course-select-label">Select Course</InputLabel>
                    <Select
                        labelId="student-select-label"
                        value={selectedCourseId.toString()}
                        label="Select Course"
                        onChange={e => setSelectedCourseId(parseInt(e.target.value))}
                        MenuProps={{
                            PaperProps: {
                                style: {
                                    maxHeight: 224
                                }
                            }
                        }}
                    >
                        {availableCourses.length > 0 ? availableCourses.map(course => (
                            <MenuItem key={course.id} value={course.id.toString()}>
                                {course.courseName}
                            </MenuItem>
                        )) : <MenuItem disabled>No courses are available to add</MenuItem>}
                    </Select>
                </FormControl>
                <Button variant="contained" color="primary" sx={{mt: 2}} onClick={handleAddCourse}>Add</Button>
            </Box>
        </Box>
    )
}

export default AddRemoveCourse