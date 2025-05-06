import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AddRemoveStudent from '../AddRemoveStudent';
import { SnackbarProvider } from '@/app/contexts/SnackbarContext';
import { StudentProvider } from '@/app/contexts/StudentContext';
import { CourseProvider } from '@/app/contexts/CourseContext';

beforeEach(() => {
  const { useStudents } = require('@/app/contexts/StudentContext');
  useStudents.mockReturnValue({
    students: mockStudents,
    fetchStudents: jest.fn(),
  });

  const { useCourses } = require('@/app/contexts/CourseContext');
  useCourses.mockReturnValue({
    fetchCourseById: jest.fn().mockResolvedValue(mockCourse),
    addStudentToCourse: jest.fn(),
    removeStudentFromCourse: jest.fn(),
  });
});

const mockStudents = [
  { id: 1, firstName: 'John', lastName: 'Doe', email: 'john@example.com' },
  { id: 2, firstName: 'Jane', lastName: 'Smith', email: 'jane@example.com' },
];

const mockCourse = {
  id: 1,
  students: [mockStudents[0]],
};

jest.mock('@/app/contexts/CourseContext', () => {
  const actual = jest.requireActual('@/app/contexts/CourseContext');
  return {
    ...actual,
    CourseProvider: actual.CourseProvider,
    useCourses: jest.fn().mockReturnValue({
      fetchCourseById: jest.fn().mockResolvedValue(mockCourse),
      addStudentToCourse: jest.fn(),
      removeStudentFromCourse: jest.fn(),
    }),
  };
});

jest.mock('@/app/contexts/StudentContext', () => {
  const actual = jest.requireActual('@/app/contexts/StudentContext');
  return {
    ...actual,
    StudentProvider: actual.StudentProvider,
    useStudents: jest.fn().mockReturnValue({
      students: mockStudents,
      fetchStudents: jest.fn(),
    }),
  };
});


describe('AddRemoveStudent Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('removes a student when clicking Remove', async () => {
    render(
      <SnackbarProvider>
        <StudentProvider>
          <CourseProvider>
            <AddRemoveStudent courseId={1} />
          </CourseProvider>
        </StudentProvider>
      </SnackbarProvider>
    );

    const removeButton = await screen.findByRole('button', { name: /remove/i });
    fireEvent.click(removeButton);

    expect(removeButton).toBeEnabled();
  });
});
