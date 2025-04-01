interface CourseFields {
    courseName: string
}
interface StudentFields {
    firstName: string;
    lastName: string;
    email: string;
}

export const validateCourseFields = (courseFields: CourseFields) => {
    let errors: Record<string, string> = {};
    const courseNameRegex = /^[a-zA-Z0-9_-]{2,25}$/;

    if (!courseFields.courseName || courseFields.courseName.trim() === "") {
        errors.courseName = "Course name cannot be blank";
    } else if (courseFields.courseName.length < 2) {
        errors.courseName = "Course name must be at least 2 characters long";
    } else if (courseFields.courseName.length > 25) {
        errors.courseName = "Course name can be maximum 25 characters long";
    } else if (!courseNameRegex.test(courseFields.courseName)) {
        errors.courseName = "Course name can contain only letters, numbers, hyphens and underscores";
    }

    return errors;
};

export const validateStudentFields = (studentFields: StudentFields) => {
    let errors: Record<string, string> = {};
    const courseNameRegex = /^[a-zA-Z]{3,20}$/;
    const emailRegex = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/;

    if (!studentFields.firstName || studentFields.firstName.trim() === "") {
        errors.firstName = "First name cannot be blank";
    } else if (studentFields.firstName.length < 3) {
        errors.firstName = "First name must be at least 3 characters long";
    } else if (studentFields.firstName.length > 20) {
        errors.firstName = "First name can be maximum 20 characters long";
    } else if (!courseNameRegex.test(studentFields.firstName)) {
        errors.firstName = "First name can contain only Roman alphabet";
    }


    if (!studentFields.lastName || studentFields.lastName.trim() === "") {
        errors.lastName = "Last name cannot be blank";
    } else if (studentFields.lastName.length < 3) {
        errors.lastName = "Last name must be at least 3 characters long";
    } else if (studentFields.lastName.length > 20) {
        errors.lastName = "Last name can be maximum 20 characters long";
    } else if (!courseNameRegex.test(studentFields.lastName)) {
        errors.lastName = "Last name can contain only Roman alphabet";
    }


    if (!studentFields.email || studentFields.email.trim() === "") {
        errors.email = "E-mail cannot be blank";
    } else if (!emailRegex.test(studentFields.email)) {
        errors.email = "E-mail is invalid";
    }

    return errors;
};

export const handleAPIResponse = async (response: Response, resourceType = "Resource") => {
    if (!response.ok) {
        if (response.status === 404) {
            throw new Error(`${resourceType} not found`);
        } else if (response.status === 400) {
            throw new Error(`Invalid input for ${resourceType}`);
        } else if (response.status === 409) {
            throw new Error(`${resourceType} already exists`);
        } else {
            throw new Error("A server error has occurred");
        }
    }
}
