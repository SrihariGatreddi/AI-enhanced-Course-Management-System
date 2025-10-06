package com.cms.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.cms.service.AiAssistantService;
import com.cms.service.ChatHistoryService;
import com.cms.service.CourseService;
import com.cms.service.StudentCourseService;
import com.cms.service.EmailService;
import com.cms.model.StudentCourse;
import com.cms.repository.UserRepository;
import com.cms.model.User;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
@RequestMapping("/student")
public class StudentController {

    private final AiAssistantService aiAssistantService;
    private final ChatHistoryService chatHistoryService;
    private final CourseService courseService;
    private final StudentCourseService studentCourseService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Autowired
    public StudentController(AiAssistantService aiAssistantService, ChatHistoryService chatHistoryService,
                            CourseService courseService, StudentCourseService studentCourseService,
                            EmailService emailService, UserRepository userRepository) {
        this.aiAssistantService = aiAssistantService;
        this.chatHistoryService = chatHistoryService;
        this.courseService = courseService;
        this.studentCourseService = studentCourseService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')") // Ensures only students can access this
    public String showStudentDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User student = userRepository.findByEmail(email).orElse(null);
        Long studentId = (student != null) ? student.getUserId() : null;
        List<Object> chatHistory = (studentId != null) ? chatHistoryService.getMessages(studentId) : List.of();
        model.addAttribute("chatHistory", chatHistory);
        List<StudentCourse> courses = (studentId != null) ? studentCourseService.getCoursesByStudentId(studentId) : List.of();
        model.addAttribute("courses", courses);
        return "student-dashboard";
    }

    @PostMapping("/dashboard/ask")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public String askAiAssistant(@RequestParam("question") String question, Model model) {
        System.out.println("hi");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Long studentId = 1L; // Replace with actual lookup
        String aiResponse = aiAssistantService.getAiResponse(studentId, question);
        chatHistoryService.addMessage(studentId, "Student: " + question);
        chatHistoryService.addMessage(studentId, "AI: " + aiResponse);
        List<Object> chatHistory = chatHistoryService.getMessages(studentId);
        model.addAttribute("chatHistory", chatHistory);
        // model.addAttribute("courses", ...); // Add registered courses here
        return "student-dashboard";
    }

    @PostMapping("/ai-response")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAiResponse(@RequestBody Map<String, String> payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("API endpoint /ai-response was called successfully!");
        String email = auth.getName();
        Long studentId = 1L; // Replace with actual lookup
        String question = payload.get("question");
        try {
            String aiResponse = aiAssistantService.getAiResponse(studentId, question);
            return ResponseEntity.ok(Map.of("answer", aiResponse));
        } catch (AiAssistantService.AiLimitExceededException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("answer", e.getMessage()));
        }
    }

    @GetMapping("/enroll-course")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public String showEnrollCoursePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User student = userRepository.findByEmail(email).orElse(null);
        String studentName = (student != null) ? student.getNameOfUser() : "Student";
        model.addAttribute("studentName", studentName);
        // Courses will be fetched via AJAX
        return "enroll-course";
    }

    @GetMapping("/enroll-course/courses")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @ResponseBody
    public List<String> getAvailableCourses() {
        return courseService.getAllCourseNames();
    }

    @PostMapping("/enroll-course")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @ResponseBody
    public ResponseEntity<String> enrollCourse(@RequestBody Map<String, String> payload) {
        String firstName = payload.get("firstName");
        String rollNumber = payload.get("rollNumber");
        String courseName = payload.get("courseName");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User student = userRepository.findByEmail(email).orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Student not found.");
        }
        // Store enrollment in DB
        StudentCourse enrollment = new StudentCourse();
        enrollment.setStudentName(firstName);
        enrollment.setRollNumber(rollNumber);
        enrollment.setCourseName(courseName);
        enrollment.setStudentId(student.getUserId());
        // TODO: Set durationOfCourse from Course entity
        enrollment.setDurationOfCourse("3 months"); // Example, replace with actual duration
        studentCourseService.enrollStudent(enrollment);
        // Send email to student
        emailService.sendCourseEnrollmentDetails(enrollment, student);
        return ResponseEntity.ok("Course enrollment is successful and course details mailed to provided mail.");
    }
}
