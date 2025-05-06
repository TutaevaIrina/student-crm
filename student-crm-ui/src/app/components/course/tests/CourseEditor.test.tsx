import React from 'react';
import { render, fireEvent, screen } from '@testing-library/react';
import CourseEditor from '../CourseEditor';

jest.mock('@/app/contexts/CourseContext', () => ({
  useCourses: () => ({
    updateCourse: jest.fn().mockResolvedValue(undefined),
  }),
}));

jest.mock('@/app/contexts/SnackbarContext', () => ({
  useSnackbar: () => ({
    showSnackbar: jest.fn(),
  }),
}));

const mockCourse = { id: 1, courseName: 'Math' };
const mockOnCancel = jest.fn();
const mockOnSave = jest.fn();

function renderComponent() {
  return render(
    <CourseEditor
      course={mockCourse}
      onCancel={mockOnCancel}
      onSave={mockOnSave}
    />
  );
}

describe('CourseEditor Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the course name input field', () => {
    renderComponent();
    const input = screen.getByLabelText(/edit course name/i);
    expect(input).toBeInTheDocument();
    expect(input).toHaveValue('Math 101');
  });

  it('allows typing a new course name', () => {
    renderComponent();
    const input = screen.getByLabelText(/edit course name/i);
    fireEvent.change(input, { target: { value: 'Physics 101' } });
    expect(input).toHaveValue('Physics 101');
  });


  it('calls onCancel when Cancel button is clicked', () => {
    renderComponent();
    const cancelButton = screen.getByRole('button', { name: /cancel/i });
    fireEvent.click(cancelButton);
    expect(mockOnCancel).toHaveBeenCalled();
  });
});
