package com.app.gighub.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.gighub.helpers.FreelancePlatformHelper;
import com.app.gighub.models.Bid;
import com.app.gighub.models.Feedback;
import com.app.gighub.models.Job;
import com.app.gighub.models.User;
import com.app.gighub.services.BidService;
import com.app.gighub.services.CategoryService;
import com.app.gighub.services.FeedbackService;
import com.app.gighub.services.JobService;

import jakarta.servlet.http.HttpServletRequest; // ✅ Updated to `jakarta.servlet`

@Controller
@RequestMapping("/job")
public class JobController extends AbstractController {

    @Autowired
    private JobService jobService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BidService bidService;

    @Autowired
    private FeedbackService feedbackService;

    @Value("${freelancer.job.page_size}")
    private int jobPageSize;

    @GetMapping
    public String listJobs2(Model model, HttpServletRequest request) {
        String pageUrl = "/job?a=a"; // Ensuring `?` in the query string for pagination
        String filt = request.getParameter("filter");
        String pPage = request.getParameter("page");

        User me = getCurrentUser();
        boolean isMyJobsPage = false;
        Map<String, Object> filter = new HashMap<>();

        if (filt != null && filt.equals("myjobs") && me != null) {
            filter.put("user", me);
            pageUrl = "/job?filter=myjobs";
            isMyJobsPage = true;
        }

        int pageNo = 1;
        try {
            if (pPage != null) {
                pageNo = Integer.parseInt(pPage);
            }
        } catch (NumberFormatException e) {
            pageNo = 1; // Default to page 1 if parsing fails
        }

        Page<Job> jobsPage = jobService.findAllPaged(filter, pageNo, jobPageSize);

        model.addAttribute("is_my_jobs_page", isMyJobsPage);
        model.addAttribute("jobs_page", jobsPage);
        model.addAttribute("page_url", pageUrl);

        return "frontend/job/jobs";
    }

    @GetMapping({"/view/{id}", "/{id}"})
    public String viewJob(Model model, @PathVariable("id") long id) {
        Job job = jobService.get(id);
        if (job == null) {
            return "redirect:/job"; // Redirect if job doesn't exist
        }

        model.addAttribute("job", job);

        // Get my bid for the job
        Bid myBid = null;
        User currentUser = super.getCurrentUser();
        if (currentUser != null) {
            myBid = bidService.getUsersBidByJob(currentUser, job);
            if (myBid != null) {
                myBid.setProposal(FreelancePlatformHelper.nl2br(myBid.getProposal()));
            }
        }

        model.addAttribute("myBid", myBid);
        model.addAttribute("me", getCurrentUser());

        // Calculate client rating
        long avgClientFeedback = 0;
        int totalFeedbackNo = 0;
        List<Feedback> feedbacks = feedbackService.findByClient(job.getAuthor());
        if (!feedbacks.isEmpty()) {
            int sum = feedbacks.stream().mapToInt(Feedback::getClientRate).sum();
            avgClientFeedback = sum / feedbacks.size();
            totalFeedbackNo = feedbacks.size();
        }

        // Calculate hire rate
        List<Job> jobs = jobService.findByAuthor(job.getAuthor());
        List<Job> hiredJobs = jobService.findHiredJobsByAuthor(job.getAuthor());
        double hireRate = jobs.isEmpty() ? 0 : (double) hiredJobs.size() / jobs.size() * 100;

        model.addAttribute("average_client_feedback_rate", avgClientFeedback);
        model.addAttribute("reviews_no", totalFeedbackNo);
        model.addAttribute("bids_no", bidService.findByJob(job).size());
        model.addAttribute("hire_rate", (int) hireRate);
        model.addAttribute("jobs_no", jobs.size());
        model.addAttribute("hired_jobs_no", hiredJobs.size());

        return "frontend/job/view_job";
    }

    @GetMapping("/create")
    public String createJob(Model model) {
        User me = getCurrentUser();
        if (me == null) {
            return "redirect:/login";  // Redirect if user is not logged in
        }
        model.addAttribute("categories", categoryService.list());
        return "frontend/job/create_job";
    }

    @PostMapping("/save")
    public String saveJob(
            @RequestParam(name = "id", required = false) Long id,
            @ModelAttribute Job job,
            Model model) {

        if (job.getTitle().isEmpty()) {
            model.addAttribute("error", "Title required");
            return "frontend/job/create_job";
        }

        // Set the current logged-in user as author
        User author = super.getCurrentUser();
        if (author == null) {
            return "redirect:/login"; // Redirect if not logged in
        }
        job.setAuthor(author);

        Job savedJob = (id != null && id > 0) ? job : jobService.add(job);

        return "redirect:/job/view/" + savedJob.getId();
    }

    @GetMapping("/bids/{jobId}")
    public String viewBids(Model model, @PathVariable("jobId") long jobId) {
        Job job = jobService.get(jobId);
        User me = getCurrentUser();

        if (me == null || job == null || !job.getAuthor().getId().equals(me.getId())) {
            return "redirect:/job/view/" + jobId;
        }

        List<Bid> bids = bidService.findByJob(job);

        model.addAttribute("job", job);
        model.addAttribute("bids", bids);

        return "frontend/job/view_bids";
    }
}
