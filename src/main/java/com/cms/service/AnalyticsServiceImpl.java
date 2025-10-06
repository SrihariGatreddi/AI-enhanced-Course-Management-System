package com.cms.service;

import com.cms.dao.AnalyticsDao;
import com.cms.model.AIRecommendationResult;
import com.cms.model.Course;
import com.cms.model.StudentCourse;
import com.cms.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnalyticsDao analyticsDao;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    @Autowired
    public AnalyticsServiceImpl(AnalyticsDao analyticsDao, EmailService emailService) {
        this.analyticsDao = analyticsDao;
        this.emailService = emailService;
    }

    @Override
    public Map<String, Long> getAnalyticsSummary() {
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalStudents", analyticsDao.countTotalStudents());
        summary.put("activeUsers", analyticsDao.countActiveUsers());
        summary.put("totalViews", analyticsDao.countTotalViews());
        summary.put("totalErrors", analyticsDao.countTotalErrors());
        return summary;
    }

    @Override
    public List<User> getAllInstructors() {
        return analyticsDao.findAllInstructors();
    }

    @Override
    public void addInstructor(User instructor) {
        analyticsDao.saveInstructor(instructor);
    }

    @Override
    public List<Course> getAllCourses() {
        return analyticsDao.findAllCourses();
    }

    @Override
    public void addCourse(Course course) {
        analyticsDao.saveCourse(course);
        // Fetch instructor and send email
        User instructor = null;
        List<User> instructors = analyticsDao.findAllInstructors();
        for (User user : instructors) {
            if (user.getUserId().equals(course.getUserId())) {
                instructor = user;
                break;
            }
        }
        if (instructor != null) {
            emailService.sendInstructorAssignment(course, instructor);
        }
    }

    @Override
    public List<StudentCourse> getAllStudentCourses() {
        return analyticsDao.findAllStudentCourses();
    }

    @Override
    public AIRecommendationResult getCourseRecommendationForStudent(Long studentId) {
        return analyticsDao.getCourseRecommendationForStudent(studentId);
    }

    @Override
    public List<StudentCourse> getAllStudents() {
        List<StudentCourse> students = analyticsDao.findAllStudents();
        logger.info("Fetched students from database: {}", students);
        return students;
    }

    @Override
    public StudentCourse getStudentById(Long studentId) {
        for (StudentCourse student : getAllStudents()) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
}
