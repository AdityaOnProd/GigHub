package com.app.gighub.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.gighub.models.Bid;
import com.app.gighub.models.Job;
import com.app.gighub.models.User;
import com.app.gighub.security.UserDetailsImpl;
import com.app.gighub.services.BidService;
import com.app.gighub.services.JobService;
import com.app.gighub.services.UserService;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    @Autowired
    private BidService bidService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @PostMapping("/job/{jobId}")
    public ResponseEntity<?> submitBid(@PathVariable("jobId") long jobId, @RequestBody Bid bid, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in to bid.");
        }

        try {
            // 1. Get the Job
            Job job = jobService.get(jobId);
            if (job == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
            }

            // 2. Get the Bidder (Currently logged-in user)
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User bidder = userService.getByEmail(userDetails.getUsername());

            // Prevent the author from bidding on their own job!
            if (job.getAuthor().getId() == bidder.getId()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "You cannot bid on your own job!");
                return ResponseEntity.badRequest().body(error);
            }

            // 3. Attach Job and Bidder, then save
            bid.setJob(job);
            bid.setUser(bidder);
            bidService.save(bid);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Bid submitted successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error submitting bid.");
        }
    }
    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getBidsForJob(@PathVariable("jobId") long jobId, Authentication authentication) {
        // 1. Ensure user is logged in
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must be logged in.");
        }

        try {
            Job job = jobService.get(jobId);
            if (job == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User currentUser = userService.getByEmail(userDetails.getUsername());

            // 2. Security Check: Only the job author can view the bids!
            if (job.getAuthor().getId() != currentUser.getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the client can view these proposals.");
            }

            // 3. Fetch and return the bids
            List<Bid> bids = bidService.findByJob(job); 
            return ResponseEntity.ok(bids);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching bids.");
        }
    }
}