package com.app.gighub.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.gighub.models.Bid;
import com.app.gighub.models.Feedback;
import com.app.gighub.models.Job;
import com.app.gighub.models.JobHistory;
import com.app.gighub.models.Profile;
import com.app.gighub.models.User;
import com.app.gighub.services.BidService;
import com.app.gighub.services.FeedbackService;
import com.app.gighub.services.JobService;
import com.app.gighub.services.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profile")
public class ProfileController extends AbstractController {

    @Autowired
    private UserService userService;

    @Autowired
    private BidService bidService;

    @Autowired
    private JobService jobService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping({"", "/{id}"})
    public String viewProfile(@PathVariable(name = "id", required = false) Long userId, Model model) {

        User loggedUser = getCurrentUser();
        if (loggedUser == null) {
            return "redirect:/login";
        }

        boolean canEdit = false;
        User user;

        if (userId == null || userId < 1) {
            user = loggedUser;
            canEdit = true;
        } else {
            user = userService.get(userId);
            if (user != null && userId.equals(loggedUser.getId())) {
                canEdit = true;
            }
        }

        if (user == null) {
            return "redirect:/";
        }

        // Ensure profile is initialized
        Profile profile = user.getProfile() != null ? user.getProfile() : new Profile();
        if (profile.getLocation() == null) profile.setLocation("");
        if (profile.getLinkedin() == null) profile.setLinkedin("");
        if (profile.getBiography() == null) profile.setBiography("");

        List<Bid> myBids = canEdit ? bidService.findByUser(user) : null;
        List<Bid> closedBids = bidService.findByClosedAndUser(1, user);
        List<Feedback> myFeedbacks = feedbackService.findByBids(closedBids);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("canEdit", canEdit);
        model.addAttribute("myBids", myBids);
        model.addAttribute("my_feedbacks", myFeedbacks);

        return "frontend/profile/view_profile";
    }

    @GetMapping("/edit")
    public String editProfile(Model model) {

        User user = getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "frontend/profile/edit_profile";
    }

    @PostMapping("/save")
    public String saveProfile(@ModelAttribute @Valid User user, @ModelAttribute Profile profile, Model model) {

        User me = getCurrentUser();
        if (me == null) {
            return "redirect:/login";
        }

        me.setName(user.getName());
        me.setEmail(user.getEmail());

        if (me.getProfile() == null) {
            me.setProfile(new Profile());
            me.getProfile().setUser(me);
        }

        // Prevent null values before saving
        me.getProfile().setLocation(profile.getLocation() != null ? profile.getLocation() : "");
        me.getProfile().setLinkedin(profile.getLinkedin() != null ? profile.getLinkedin() : "");
        me.getProfile().setBiography(profile.getBiography() != null ? profile.getBiography() : "");

        userService.save(me);
        return "redirect:/profile";
    }

    @GetMapping("/client/{id}")
    public String viewClientProfile(@PathVariable("id") Long userId, Model model) throws Exception {

        User user = userService.get(userId);
        if (user == null) {
            throw new Exception("User not found");
        }

        List<Job> clientJobs = jobService.findByAuthor(user);
        List<JobHistory> jobHistory = new ArrayList<>();

        int totalJobs = clientJobs.size();
        int hiredJobs = jobService.findHiredJobsByAuthor(user).size();

        for (Job j : clientJobs) {
            JobHistory jh = new JobHistory();
            Feedback feedback = feedbackService.findByJob(j);

            jh.setJob(j);
            jh.setFeedback(feedback);
            jobHistory.add(jh);
        }

        // Ensure profile is initialized
        Profile profile = user.getProfile() != null ? user.getProfile() : new Profile();
        if (profile.getLocation() == null) profile.setLocation("");
        if (profile.getLinkedin() == null) profile.setLinkedin("");
        if (profile.getBiography() == null) profile.setBiography("");

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("total_jobs", totalJobs);
        model.addAttribute("hired_jobs", hiredJobs);
        model.addAttribute("job_history", jobHistory);

        return "frontend/profile/view_client_profile";
    }
}
