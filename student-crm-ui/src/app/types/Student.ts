import {CourseSimple} from "@/app/types/CourseSimple";

export interface Student {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    courses: CourseSimple[]
}