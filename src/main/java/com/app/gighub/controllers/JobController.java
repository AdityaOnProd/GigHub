package com.app.gighub.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import com.app.gighub.models.Job;
import com.app.gighub.services.JobService;

// 1. Mark as RestController
@RestController
// 2. Prefix with /api/jobs
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    // 3. Return the list of jobs as JSON!
    @GetMapping
    public List<Job> getAllJobs() {
        // We are using your existing jobService.list() method
        return jobService.list();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) {
        Job job = jobService.get(id);
        if (job == null) {
            return ResponseEntity.notFound().build(); // Returns 404 if job doesn't exist
        }
        return ResponseEntity.ok(job);
    }
}