package com.cms.dao;

import com.cms.model.User;
import com.cms.model.Course;
import com.cms.model.StudentCourse;
import com.cms.model.AIRecommendationResult;

import java.util.List;

public interface AnalyticsDao {
    long countTotalStudents();
    long countActiveUsers();
    long countTotalViews();
    long countTotalErrors();
    List<User> findAllInstructors();
    void saveInstructor(User instructor);
    List<Course> findAllCourses();
    void saveCourse(Course course);
    List<StudentCourse> findAllStudentCourses();
    AIRecommendationResult getCourseRecommendationForStudent(Long studentId);
    List<StudentCourse> findAllStudents();
}
