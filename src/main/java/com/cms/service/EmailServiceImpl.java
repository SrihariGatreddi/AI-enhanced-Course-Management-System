package com.cms.service;

import com.cms.model.Course;
import com.cms.model.StudentCourse;
import com.cms.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private  JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendRegistrationConfirmation(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Registration Successful!");
        message.setText("Dear " + user.getNameOfUser() + ",\n\nWelcome to our Course Management System!\n\n"
                + "Your registration is complete. Your Student ID is: " + user.getUserId() + "\n"
                + "You can now log in with your email and password.\n\n"
                + "Happy learning!");
        mailSender.send(message);
    }

    @Override
    public void sendCourseEnrollmentDetails(StudentCourse enrollment, User student) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("Course Enrollment Confirmation: " + enrollment.getCourseName());
        message.setText("Dear " + student.getNameOfUser() + ",\n\nYou have successfully enrolled in the following course:\n\n"
                + "Course: " + enrollment.getCourseName() + "\n"
                + "Duration: " + enrollment.getDurationOfCourse() + "\n\n"
                + "We wish you the best in your studies!");
        mailSender.send(message);
    }

    @Override
    public void sendInstructorAssignment(Course course, User instructor) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(instructor.getEmail());
        message.setSubject("You have been assigned a new course!");
        message.setText("Dear " + instructor.getNameOfUser() + ",\n\nYou have been assigned to teach the following course:\n\n"
                + "Course Name: " + course.getCourseName() + "\n"
                + "Duration: " + course.getDurationOfCourse() + "\n\n"
                + "Your login details are:\n"
                + "Email: " + instructor.getEmail() + "\n"
                + "Password: " + instructor.getPassword() + "\n\n"
                + "Please log in to the dashboard to view your course materials.");
        mailSender.send(message);
    }

    @Override
    public void sendCourseRecommendation(User student, String recommendationBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(student.getEmail());
        message.setSubject("New Course Recommendations For You");
        message.setText("Dear " + student.getNameOfUser() + ",\n\n" + recommendationBody);
        mailSender.send(message);
    }
}
