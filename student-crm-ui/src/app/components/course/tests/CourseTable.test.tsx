import React from 'react';
import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import CourseTable from '../CourseTable';

const mockFetchCourses = jest.fn();
const mockDeleteCoursesById = jest.fn().mockResolvedValue({ success: true });
const mockUpdateCourse = jest.fn().mockResolvedValue({ success: true });


jest.mock('@/app/contexts/CourseContext', () => ({
  useCourses: () => ({
    courses: [
      { id: 1, courseName: 'Math 101', students: [{ id: 1 }, { id: 2 }] },
      { id: 2, courseName: 'Physics 202', students: [] },
    ],
    fetchCourses: mockFetchCourses,
    deleteCoursesById: mockDeleteCoursesById,
    updateCourse: mockUpdateCourse,
  }),
}));

const mockShowSnackbar = jest.fn();
jest.mock('@/app/contexts/SnackbarContext', () => ({
  useSnackbar: () => ({
    showSnackbar: mockShowSnackbar,
  }),
}));

describe('CourseTable', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders course list', () => {
    render(<CourseTable />);
    expect(screen.getByText('Math 101')).toBeInTheDocument();
    expect(screen.getByText('Physics 202')).toBeInTheDocument();
  });

  it('filters courses by search term', () => {
    render(<CourseTable />);
    const searchInput = screen.getByPlaceholderText(/search a course/i);
    fireEvent.change(searchInput, { target: { value: 'math' } });
    expect(screen.getByText('Math 101')).toBeInTheDocument();
    expect(screen.queryByText('Physics 202')).toBeNull();
  });
});
