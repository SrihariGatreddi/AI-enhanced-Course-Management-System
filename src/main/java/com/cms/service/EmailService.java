package com.cms.service;

import com.cms.model.Course;
import com.cms.model.StudentCourse;
import com.cms.model.User;

public interface EmailService {
    void sendRegistrationConfirmation(User user);
    void sendCourseEnrollmentDetails(StudentCourse enrollment, User student);
    void sendInstructorAssignment(Course course, User instructor);
    void sendCourseRecommendation(User student, String recommendationBody);
}

