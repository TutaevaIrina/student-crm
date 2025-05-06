"use client"

import {createContext, ReactNode, useCallback, useContext, useState} from "react";
import {Student} from "@/app/types/Student";

type StudentContextType = {
    students: Student[];
    fetchStudents: () => Promise<void>;
    createStudent: (firstName: string, lastName: string, email: string) => Promise<void>;
    fetchStudentById: (studentId: number) => Promise<any>;
    fetchStudentsByName: (name: string) => Promise<void>;
    updateStudent: (studentId: number, firstName: string, lastName: string, email: string) => Promise<void>;
    deleteStudentById: (studentId: number) => Promise<void>
};
const StudentContext = createContext<StudentContextType | undefined>(undefined);

export const StudentProvider: React.FC<{ children: ReactNode }> = ({children}) => {
    const [students, setStudents] = useState<Student[]>([]);


    const fetchStudents = useCallback(async () => {
        try {
            const response = await fetch("http://localhost:8080/student-api/students");
            if (!response.ok) {
                throw new Error("A server error has occurred")
            }
            const data = await response.json();
            setStudents(data)
        } catch (error: any) {
            throw error;
        }
    }, []);

    const createStudent = useCallback(async (firstName: string, lastName: string, email: string) => {
        try {
            const newStudent = {firstName, lastName, email};
            const response = await fetch("http://localhost:8080/student-api/student", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(newStudent)
            });

            if (response.status === 409) {
                throw new Error("E-mail already exists");
            } else if (response.status === 400) {
                throw new Error("Student name is blank, too short or contains invalid signs");
            } else if (!response.ok) {
                throw new Error("A server error has occurred");
            } else {
                const result = await response.json()
                setStudents(students => [...students, result]);
            }
        } catch (error: any) {
            throw error;
        }
    }, [fetchStudents]);

    const fetchStudentById = useCallback(async (studentId: number) => {
        try {
            const response = await fetch(`http://localhost:8080/student-api/student/${studentId}`);
            if (response.status === 404) {
                throw new Error("Student not found")
            } else if (!response.ok) {
                throw new Error("A server error has occurred");
            } else {
                return await response.json();
            }
        } catch (error: any) {
            throw error;
        }
    }, []);

    const fetchStudentsByName = async (name: string) => {
        const response = await fetch(`http://localhost:8080/student-api/students/${name}`);
        return await response.json()
    };

    const updateStudent = useCallback(async (studentId: number, firstName: string, lastName: string, email: string) => {
        try {
            const updateStudent = {firstName, lastName, email};
            const response = await fetch(`http://localhost:8080/student-api/student/${studentId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(updateStudent)
            });
            if (response.status === 409) {
                throw new Error("E-mail already exists");
            } else if (response.status === 400) {
                throw new Error("Student name is blank, too short or contains invalid signs");
            } else if (response.status === 404) {
                throw new Error("Student not found");
            } else if (!response.ok) {
                throw new Error("A server error has occurred");
            } else {
                const updatedStudent = await response.json()
                setStudents(students => students.map(student => student.id === studentId ? updatedStudent : student));
            }
        } catch (error: any) {
            throw error;
        }
    }, [fetchStudents]);


    const deleteStudentById = useCallback(async (studentId: number) => {
        try {
            const response = await fetch(`http://localhost:8080/student-api/student/${studentId}`, {
                method: "DELETE"
            });
            if (response.status === 404) {
                throw new Error("Student not found")
            } else if (!response.ok) {
                throw new Error("A server error has occurred");
            } else {
                setStudents(students => students.filter(student => student.id !== studentId))
            }
        } catch (error: any) {
            throw error;
        }
    }, [fetchStudents]);


    return (
        <StudentContext.Provider value={{
            students,
            fetchStudents,
            createStudent,
            fetchStudentById,
            fetchStudentsByName,
            updateStudent,
            deleteStudentById
        }}>
            {children}
        </StudentContext.Provider>
    );
}
export const useStudents = () => {
    const context = useContext(StudentContext);
    if (!context) {
        throw new Error('useStudents must be used within a StudentProvider');
    }
    return context;
};
