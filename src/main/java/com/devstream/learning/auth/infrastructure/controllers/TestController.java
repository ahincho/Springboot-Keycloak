package com.devstream.learning.auth.infrastructure.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin_role')")
    public String helloAdmin() {
        return "Hello Admin!";
    }
    @GetMapping("/teacher")
    @PreAuthorize("hasRole('teacher_role')")
    public String helloTeacher() {
        return "Hello Teacher!";
    }
    @GetMapping("/student")
    @PreAuthorize("hasRole('student_role')")
    public String helloStudent() {
        return "Hello Student!";
    }
}
