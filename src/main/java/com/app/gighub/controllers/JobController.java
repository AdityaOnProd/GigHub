package com.app.gighub.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.app.gighub.models.Job;
import com.app.gighub.models.User;
import com.app.gighub.security.UserDetailsImpl;
import com.app.gighub.services.JobService;
import com.app.gighub.services.UserService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
        Job job = jobService.get(id);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(job);
    }

    // --- NEW POST ENDPOINT ---
    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job, Authentication authentication) {
        // 1. Ensure the user is actually logged in
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to post a job.");
        }

        try {
            // 2. Get the full User object of the logged-in person
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User author = userService.getByEmail(userDetails.getUsername());
            
            // 3. Attach the author to the job and save it
            job.setAuthor(author);
            Job savedJob = jobService.add(job);
            
            return ResponseEntity.ok(savedJob);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating job.");
        }
    }
}