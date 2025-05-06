import '@testing-library/jest-dom';

import { render, screen, fireEvent } from '@testing-library/react';
import CourseCreator from '../CourseCreator';
import { CourseProvider } from '@/app/contexts/CourseContext';
import { SnackbarProvider } from '@/app/contexts/SnackbarContext';

describe('CourseCreator', () => {
  it('renders input and button', () => {
    render(
      <SnackbarProvider>
        <CourseProvider>
          <CourseCreator />
        </CourseProvider>
      </SnackbarProvider>
    );

    expect(screen.getByLabelText(/course name/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /create course/i })).toBeInTheDocument();
  });

  it('shows error when input is empty and button is clicked', async () => {
    render(
      <SnackbarProvider>
        <CourseProvider>
          <CourseCreator />
        </CourseProvider>
      </SnackbarProvider>
    );

    const button = screen.getByRole('button', { name: /create course/i });
    fireEvent.click(button);

    expect(await screen.findByText(/cannot be blank/i)).toBeInTheDocument();
  });

  it('clears input when clear button is clicked', () => {
    render(
      <SnackbarProvider>
        <CourseProvider>
          <CourseCreator />
        </CourseProvider>
      </SnackbarProvider>
    );

    const input = screen.getByLabelText(/course name/i);
    fireEvent.change(input, { target: { value: 'Math' } });
    expect(input).toHaveValue('Math');

    const clearButton = screen.getByTestId('ClearIcon').closest('button');
    fireEvent.click(clearButton!);
    expect(input).toHaveValue('');
  });
});
