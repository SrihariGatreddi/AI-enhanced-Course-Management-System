package com.cms.controller;

import com.cms.model.User;
import com.cms.model.Course;
import com.cms.model.StudentCourse;
import com.cms.service.AnalyticsService;
import com.cms.service.AiAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AnalyticsService analyticsService;
    private final AiAssistantService aiAssistantService;

    @Autowired
    public AdminController(AnalyticsService analyticsService, AiAssistantService aiAssistantService) {
        this.analyticsService = analyticsService;
        this.aiAssistantService = aiAssistantService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Ensures only admins can access this
    public String showAdminDashboard(Model model) {
        // Fetch the analytics summary from the service
        Map<String, Long> summary = analyticsService.getAnalyticsSummary();

        // Add each metric to the model for the view to use
        model.addAttribute("totalStudents", summary.get("totalStudents"));
        model.addAttribute("activeUsers", summary.get("activeUsers"));
        model.addAttribute("totalViews", summary.get("totalViews"));
        model.addAttribute("totalErrors", summary.get("totalErrors"));

        List<StudentCourse> students = analyticsService.getAllStudents();

        model.addAttribute("instructors", analyticsService.getAllInstructors());
        model.addAttribute("courses", analyticsService.getAllCourses());
        model.addAttribute("students", students);

        return "admin-dashboard";
    }

    @PostMapping("/dashboard/instructors/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addInstructor(@ModelAttribute User instructor, RedirectAttributes redirectAttributes) {
        analyticsService.addInstructor(instructor); // Service method to add instructor
        redirectAttributes.addFlashAttribute("message", "Instructor added successfully.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/dashboard/courses/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String addCourse(@ModelAttribute Course course, RedirectAttributes redirectAttributes) {
        analyticsService.addCourse(course); // Service method to add course
        redirectAttributes.addFlashAttribute("message", "Course added successfully.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/dashboard/recommend")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String processRecommendCourse(@RequestParam("studentId") Long studentId, Model model) {
        StudentCourse student = analyticsService.getStudentById(studentId);
        String recommendation = aiAssistantService.getCourseRecommendation(student.getCourseHistory());
        String emailDraft = aiAssistantService.getEmailDraft(student.getStudentName(), recommendation);
        model.addAttribute("recommendation", recommendation);
        model.addAttribute("emailDraft", emailDraft);
        // Also re-populate dashboard data
        Map<String, Long> summary = analyticsService.getAnalyticsSummary();
        model.addAttribute("totalStudents", summary.get("totalStudents"));
        model.addAttribute("activeUsers", summary.get("activeUsers"));
        model.addAttribute("totalViews", summary.get("totalViews"));
        model.addAttribute("totalErrors", summary.get("totalErrors"));
        model.addAttribute("instructors", analyticsService.getAllInstructors());
        model.addAttribute("courses", analyticsService.getAllCourses());
        model.addAttribute("students", analyticsService.getAllStudents());
        return "admin-dashboard";
    }
}
