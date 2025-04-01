"use client"

import {useEffect, useState} from "react";
import {Student} from "@/app/types/Student";
import {useStudents} from "@/app/contexts/StudentContext";
import StudentEditor from "@/app/components/student/StudentEditor";
import AddRemoveCourse from "@/app/components/student/AddRemoveCourse";
import {Box, Button, Typography} from "@mui/material";
import Dashboard from "@/app/components/common/Dashboard";

function StudentIdPage({params}: { params: { studentId: number } }) {
    const [student, setStudent] = useState<Student | null>(null)
    const [isEditing, setIsEditing] = useState(false)
    const {fetchStudentById, updateStudent} = useStudents()

    useEffect(() => {
        const fetchData = async () => {
            const data = (await fetchStudentById(params.studentId)) as Student;
            setStudent(data);
        };

        fetchData();
    }, [params.studentId]);

    const startEditing = () => {
        setIsEditing(true)
    }

    const stopEditing = () => {
        setIsEditing(false)
    }

    const handleSave = async (newFirstName: string, newLastName: string, newEmail: string) => {
        await updateStudent(params.studentId, newFirstName, newLastName, newEmail);
        setStudent(prevStudent => {
            if (prevStudent === null) return null;
            return {...prevStudent, firstName: newFirstName, lastName: newLastName, email: newEmail}
        })
        stopEditing();
    }


    if (!student) {
        return <Typography>Loading...</Typography>;
    }
    console.log(student)

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
                <StudentEditor
                    student={student}
                    onCancel={stopEditing}
                    onSave={handleSave}/>
            ) : (
                <Box sx={{ display: 'flex' }}>
                    <Box sx={{ display: 'flex', flexDirection: 'column'}}>
                        <Typography variant="h5" color="text.primary">
                            Nr. {params.studentId} {student.firstName} {student.lastName}
                        </Typography>
                        <Typography variant="h6" color="text.primary" gutterBottom>
                            e-mail: {student.email}
                        </Typography>
                        <Box sx={{ display: 'flex', flexDirection: 'row', gap: 1 }}>
                        <Button variant="outlined" onClick={startEditing}>Edit</Button>
                        </Box>
                    </Box>
                </Box>
            )}
            <AddRemoveCourse studentId={params.studentId}/>
            </Box>
        </Box>
    );
}

export default StudentIdPage;
