package com.cms.dao;

import com.cms.model.AIRecommendationResult;
import com.cms.model.Course;
import com.cms.model.StudentCourse;
import com.cms.model.User;
import com.cms.repository.ActiveSessionRepository;
import com.cms.repository.CourseRepository;
import com.cms.repository.ErrorLogRepository;
import com.cms.repository.StudentCourseRepository;
import com.cms.repository.UserRepository;
import com.cms.repository.ViewLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnalyticsDaoImpl implements AnalyticsDao {

    private final UserRepository userRepository;
    private final ActiveSessionRepository activeSessionRepository;
    private final ViewLogRepository viewLogRepository;
    private final ErrorLogRepository errorLogRepository;
    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;

    @Autowired
    public AnalyticsDaoImpl(UserRepository userRepository,
                            ActiveSessionRepository activeSessionRepository,
                            ViewLogRepository viewLogRepository,
                            ErrorLogRepository errorLogRepository,
                            CourseRepository courseRepository,
                            StudentCourseRepository studentCourseRepository) {
        this.userRepository = userRepository;
        this.activeSessionRepository = activeSessionRepository;
        this.viewLogRepository = viewLogRepository;
        this.errorLogRepository = errorLogRepository;
        this.courseRepository = courseRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    @Override
    public long countTotalStudents() {
        // Assuming a method exists in UserRepository to count users by role
        return userRepository.countByRole("ROLE_STUDENT");
    }

    @Override
    public long countActiveUsers() {
        return activeSessionRepository.count();
    }

    @Override
    public long countTotalViews() {
        return viewLogRepository.count();
    }

    @Override
    public long countTotalErrors() {
        return errorLogRepository.count();
    }

    @Override
    public List<User> findAllInstructors() {
        return userRepository.findByRole("ROLE_INSTRUCTOR");
    }

    @Override
    public void saveInstructor(User instructor) {
        instructor.setRole("ROLE_INSTRUCTOR");
        userRepository.save(instructor);
    }

    @Override
    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    @Override
    public List<StudentCourse> findAllStudentCourses() {
        return studentCourseRepository.findAll();
    }

    @Override
    public AIRecommendationResult getCourseRecommendationForStudent(Long studentId) {
        // Placeholder for AI integration logic
        return new AIRecommendationResult("Recommended courses...", "Email draft...");
    }
    @Override
    public List<StudentCourse> findAllStudents() {
        return studentCourseRepository.findAll();
    }
}
