package com.cms.service;

import com.cms.model.Course;
import com.cms.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<String> getAllCourseNames() {
        return courseRepository.findAll()
                .stream()
                .map(Course::getCourseName)
                .collect(Collectors.toList());
    }
}

