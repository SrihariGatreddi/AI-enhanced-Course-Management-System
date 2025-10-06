package com.cms.service;

import com.cms.model.StudentCourse;
import com.cms.repository.StudentCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentCourseServiceImpl implements StudentCourseService {
    private final StudentCourseRepository studentCourseRepository;

    @Autowired
    public StudentCourseServiceImpl(StudentCourseRepository studentCourseRepository) {
        this.studentCourseRepository = studentCourseRepository;
    }

    @Override
    public void enrollStudent(StudentCourse studentCourse) {
        studentCourseRepository.save(studentCourse);
    }

    @Override
    public List<StudentCourse> getCoursesByStudentId(Long studentId) {
        return studentCourseRepository.findByStudentId(studentId);
    }
}
