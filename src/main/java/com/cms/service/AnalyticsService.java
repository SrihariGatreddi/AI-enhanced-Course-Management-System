package com.cms.service;

import com.cms.model.AIRecommendationResult;
import com.cms.model.Course;
import com.cms.model.StudentCourse;
import com.cms.model.User;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {
    Map<String, Long> getAnalyticsSummary();

    List<User> getAllInstructors();

    void addInstructor(User instructor);

    List<Course> getAllCourses();

    void addCourse(Course course);

    List<StudentCourse> getAllStudentCourses();

    AIRecommendationResult getCourseRecommendationForStudent(Long studentId);

    List<StudentCourse> getAllStudents();

    StudentCourse getStudentById(Long studentId);
}
