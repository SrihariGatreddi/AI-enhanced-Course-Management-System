document.addEventListener('DOMContentLoaded', () => {
    // --- NEW: Read the CSRF token from the meta tags ---
    // This assumes you've added the meta tags to your enroll-course.html <head>
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Fetch student name (implementation pending)
    const studentNameEl = document.getElementById('student-name');
    // TODO: Set student name from backend

    // Fetch available courses for the dropdown
    fetch('/student/enroll-course/courses')
        .then(response => {
            if (!response.ok) throw new Error('Could not load courses.');
            return response.json();
        })
        .then(courses => {
            const courseSelect = document.getElementById('courseSelect');
            courseSelect.innerHTML = '<option value="">Select a course</option>'; // Add a default placeholder
            courses.forEach(course => {
                const option = document.createElement('option');
                option.value = course;
                option.textContent = course;
                courseSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error fetching courses:', error);
            document.getElementById('courseSelect').innerHTML = '<option value="">Failed to load courses</option>';
        });

    // --- UPDATED: Handle form submission with CSRF token and better error handling ---
    document.getElementById('enroll-form').addEventListener('submit', async function(e) {
        e.preventDefault();

        const firstName = document.getElementById('firstName').value;
        const rollNumber = document.getElementById('rollNumber').value;
        const courseName = document.getElementById('courseSelect').value;

        if (!courseName) {
            alert('Please select a course.');
            return;
        }

        try {
            const response = await fetch('/student/enroll-course', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // Add the CSRF token to the request headers
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({ firstName, rollNumber, courseName })
            });

            if (response.ok) {
                const successMessage = await response.text();
                alert(successMessage || 'Enrollment successful!');
                document.getElementById('enroll-form').reset();
            } else {
                // Handle non-successful responses (like 400, 403, 500)
                const errorText = await response.text();
                alert(`Error: ${errorText}`);
            }
        } catch (error) {
            console.error('Error enrolling in course:', error);
            alert('An error occurred while trying to enroll. Please try again.');
        }
    });
});