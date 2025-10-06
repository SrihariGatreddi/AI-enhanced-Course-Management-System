package com.cms.service;

import com.cms.model.StudentCourse;

import java.util.List;

public interface StudentCourseService {
    void enrollStudent(StudentCourse studentCourse);

    List<StudentCourse> getCoursesByStudentId(Long studentId);
}
