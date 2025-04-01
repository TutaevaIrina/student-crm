'use client'

import {createContext, ReactNode, useCallback, useContext, useEffect, useState} from "react";
import {Course} from "@/app/types/Course";

interface CourseContextType {
    courses: Course[];
    fetchCourses: () => Promise<void>;
    createCourse: (courseName: string) => Promise<void>;
    fetchCourseById: (courseId: number) => Promise<Course>;
    fetchCoursesByName: (courseName: string) => Promise<any>;
    updateCourse: (courseId: number, courseName: string) => Promise<void>;
    deleteCoursesById: (courseId: number) => Promise<void>;
    addStudentToCourse: (courseId: number, studentId: number) => Promise<void>;
    removeStudentFromCourse: (courseId: number, studentId: number) => Promise<void>
}

const CourseContext = createContext<CourseContextType | undefined>(undefined);
export const CourseProvider: React.FC<{ children: ReactNode }> = ({children}) => {
        const [courses, setCourses] = useState<Course[]>([]);

        const fetchCourses = useCallback(async () => {
            try {
                const response = await fetch("http://localhost:8080/course-api/courses");
                if (!response.ok) {
                    throw new Error("A server error has occurred");
                }
                const data = await response.json();
                setCourses(data)
            } catch (error: any) {
                throw error;
            }
        }, []);


        const createCourse = useCallback(async (courseName: string) => {
            try {
                const newCourse = {courseName};
                const response = await fetch("http://localhost:8080/course-api/course", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(newCourse)
                });

                if (response.status === 409) {
                    throw new Error("Course with this name already exists");
                } else if (response.status === 400) {
                    throw new Error("Course name is blank, too short or contains invalid signs");
                } else if (!response.ok) {
                    throw new Error("A server error has occurred");
                } else {
                    const result = await response.json()
                    setCourses(courses => [...courses, result]);
                }
            } catch (error: any) {
                throw error;
            }
        }, [fetchCourses]);


        const fetchCourseById = useCallback(async (courseId: number) => {
            try {
                const response = await fetch(`http://localhost:8080/course-api/course/${courseId}`);
                if (response.status === 404) {
                    throw new Error("Course not found")
                } else if (!response.ok) {
                    throw new Error("A server error has occurred");
                } else {
                    return await response.json();
                }
            } catch (error: any) {
                throw error;
            }
        }, []);


        const fetchCoursesByName = async (courseName: string) => {
            const response = await fetch(`http://localhost:8080/course-api/courses/${courseName}`);
            return await response.json()
        };


        const updateCourse = useCallback(async (courseId: number, courseName: string) => {
            try {
                const updateCourse = {courseName};
                const response = await fetch(`http://localhost:8080/course-api/course/${courseId}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(updateCourse)
                });
                if (response.status === 409) {
                    throw new Error("Course with this name already exists");
                } else if (response.status === 400) {
                    throw new Error("Course name is blank, too short or contains invalid signs");
                } else if (response.status === 404) {
                    throw new Error("Course not found");
                } else if (!response.ok) {
                    throw new Error("A server error has occurred");
                } else {
                    const updatedCourse = await response.json()
                    setCourses(courses => courses.map(course => course.id === courseId ? updatedCourse : course));
                }

            } catch (error: any) {
                throw error;
            }
        }, [fetchCourses]);


        const deleteCoursesById = useCallback(async (courseId: number) => {
            try {
                const response = await fetch(`http://localhost:8080/course-api/course/${courseId}`, {
                    method: "DELETE"
                });
                if (response.status === 409) {
                    throw new Error("Course cannot be deleted, because students enrolled in it")
                } else if (response.status === 404) {
                    throw new Error("Course not found")
                } else if (!response.ok) {
                    throw new Error("A server error has occurred");
                } else {
                    setCourses(courses => courses.filter(course => course.id !== courseId));
                }
            } catch (error: any) {
                throw error;
            }
        }, [fetchCourses]);


        const addStudentToCourse = useCallback(async (courseId: number, studentId: number) => {
            try {
                const response = await fetch(`http://localhost:8080/course-api/course/${courseId}/student/${studentId}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    }
                });
                if (response.status === 404) {
                    throw new Error("Student or course not found")
                } else if (response.status === 409) {
                    throw new Error("Student already enrolled in the course")
                } else if (!response.ok) {
                    throw new Error("A server error has occurred")
                }
            } catch (error: any) {
                throw error;
            }
        }, [fetchCourses]);


        const removeStudentFromCourse = useCallback(async (courseId: number, studentId: number) => {
            try {
                const response = await fetch(`http://localhost:8080/course-api/course/${courseId}/student/${studentId}`, {
                    method: "DELETE"
                });
                if (response.status === 404) {
                    throw new Error("Student or course not found")
                } else if (response.status === 409) {
                    throw new Error("Student does not enrolled in the course")
                } else if (!response.ok) {
                    throw new Error("A server error has occurred")
                }
            } catch (error: any) {
                throw error;
            }
        }, []);


        return (
            <CourseContext.Provider value={{
                courses,
                fetchCourses,
                createCourse,
                fetchCourseById,
                fetchCoursesByName,
                updateCourse,
                deleteCoursesById,
                addStudentToCourse,
                removeStudentFromCourse
            }}>
                {children}
            </CourseContext.Provider>
        );
    }
;
export const useCourses = () => {
    const context = useContext(CourseContext);
    if (!context) {
        throw new Error('useCourses must be used within a CourseProvider');
    }
    return context;
};