package com.app.gighub.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.gighub.helpers.FreelancePlatformHelper;
import com.app.gighub.models.Bid;
import com.app.gighub.models.Job;
import com.app.gighub.models.User;
import com.app.gighub.services.BidService;
import com.app.gighub.services.JobService;

import jakarta.servlet.http.HttpServletRequest;  // ✅ Updated
import jakarta.validation.Valid;  // ✅ Updated

@Controller
@RequestMapping("/bid")
public class BidController extends AbstractController {

    @Autowired
    private BidService bidService;

    @Autowired
    private JobService jobService;

    @PostMapping("/save")
    public String saveBid(@Valid Bid bid, @RequestParam("job_id") Long jobId) {
        User user = super.getCurrentUser();
        if (user == null) {
            return "redirect:/login";  // Redirect if not logged in
        }

        Job job = jobService.get(jobId);
        if (job == null) {
            return "redirect:/";  // Job not found, prevent NullPointerException
        }

        bid.setJob(job);
        bid.setUser(user);
        bid.setCreated(FreelancePlatformHelper.getCurrentMySQLDate());

        bidService.save(bid);
        return "redirect:/job/view/" + job.getId();
    }

    @GetMapping("/accept/{bidId}")
    public String acceptBid(Model model, @PathVariable("bidId") long bidId, HttpServletRequest request) {
        User me = getCurrentUser();
        Bid bid = bidService.get(bidId);

        if (me == null || bid == null || bid.getJob() == null || !me.getId().equals(bid.getJob().getAuthor().getId())) {
            return "redirect:" + (request.getHeader("Referer") != null ? request.getHeader("Referer") : "/");
        }

        boolean saved = bidService.acceptBid(bid);
        if (!saved) {
            return "redirect:" + request.getHeader("Referer");
        }

        return "redirect:/message/job_room/" + bid.getJob().getId() + "/" + bid.getUser().getId();
    }

    @GetMapping("/my-contracts")
    public String myContracts(Model model) {
        User me = getCurrentUser();
        if (me == null) {
            return "redirect:/login";  // Redirect if user is not logged in
        }

        Set<Bid> contracts = new HashSet<>(bidService.findByUser(me));
        contracts.addAll(bidService.findByUserJobs(me)); // Join results

        contracts.removeIf(bid -> bid.getAccepted() == 0);  // Remove unaccepted bids

        model.addAttribute("contracts", contracts);
        model.addAttribute("me", me);
        return "frontend/bid/my_contracts";
    }

    @GetMapping("/close/{bidId}")
    public String close(@PathVariable("bidId") long bidId, HttpServletRequest request) {
        User me = getCurrentUser();
        Bid bid = bidService.get(bidId);

        if (me == null || bid == null || bid.getJob() == null || !bid.getJob().getAuthor().getId().equals(me.getId())) {
            return "redirect:" + (request.getHeader("Referer") != null ? request.getHeader("Referer") : "/");
        }

        if (bid.getClosed() == 1) {
            return "redirect:/feedback/view/" + bidId;
        }

        bid.setClosed(1);
        bidService.save(bid);
        return "redirect:/feedback/" + bidId;
    }
}
