import {StudentSimple} from "@/app/types/StudentSimple";
export interface Course {
    id: number;
    courseName: string;
    students: StudentSimple[]
}