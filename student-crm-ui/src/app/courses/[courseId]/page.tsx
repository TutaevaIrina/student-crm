"use client"

import {use, useEffect, useState} from "react";
import {Course} from "@/app/types/Course";
import {useCourses} from "@/app/contexts/CourseContext";
import AddRemoveStudent from "@/app/components/course/AddRemoveStudent";
import CourseEditor from "@/app/components/course/CourseEditor";
import {Box, Button, Typography} from "@mui/material";
import Dashboard from "@/app/components/common/Dashboard";

function CourseIdPage({params}: { params: Promise<{ courseId: number }> }) {
    const { courseId } = use(params);
    const [course, setCourse] = useState<Course | null>(null)
    const [isEditing, setIsEditing] = useState(false)
    const {fetchCourseById, updateCourse} = useCourses()

    useEffect(() => {
        const fetchData = async () => {
            const data = await fetchCourseById(courseId);
            setCourse(data);
        };

        fetchData();
    }, [courseId]);

    const startEditing = () => {
        setIsEditing(true)
    }

    const stopEditing = () => {
        setIsEditing(false)
    }

    const handleSave = async (newCourseName: string) => {
        await updateCourse(courseId, newCourseName);
        setCourse(prevCourse => {
            if (prevCourse === null) return null;
            return {...prevCourse, courseName: newCourseName}
        });
        stopEditing();
    }


    if (!course) {
        return <div>Loading...</div>;
    }

    return (
        <Box sx={{
            display: 'flex',
            height: '100vh',
            width: '100%',
            overflowY: 'auto',
            bgcolor: 'background.default',
        }}>
            <Box sx={{ width: '10%', p: 2 }}>
                <Dashboard />
            </Box>
            <Box sx={{ flexGrow: 1, p: 3, display: 'flex', flexDirection: 'column'}}>
            {isEditing ? (
                <CourseEditor
                    course={course}
                    onSave={handleSave}
                    onCancel={stopEditing}/>
            ) : (
                <Box sx={{ display: 'center', flexDirection: 'row', gap: 1 }}>
                    <Typography variant="h5" color="text.primary" gutterBottom>
                       Nr. {courseId} {course.courseName}
                    </Typography>
                    <Button variant="outlined" onClick={startEditing}>
                        Edit
                    </Button>
                </Box>
            )}
                <AddRemoveStudent courseId={courseId}/>
            </Box>

        </Box>
    );
}

export default CourseIdPage;
