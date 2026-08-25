package com.app.gighub.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.gighub.models.Bid;
import com.app.gighub.models.Job;
import com.app.gighub.models.Message;
import com.app.gighub.models.User;
import com.app.gighub.services.BidService;
import com.app.gighub.services.JobService;
import com.app.gighub.services.MessageService;
import com.app.gighub.services.UserService;

import jakarta.servlet.http.HttpServletRequest;  // ✅ Updated for Spring Boot 3+

@Controller
@RequestMapping("/message")
public class MessageController extends AbstractController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private BidService bidService;

    @Value("${freelancer.message_room.page_size}")
    private int messageRoomPageSize;

    @GetMapping
    public String myMessageRooms(Model model) {
        User me = getCurrentUser();
        if (me == null) {
            return "redirect:/login";  // ✅ Prevents NullPointerException
        }

        List<Message> messages = messageService.getRoomsByUser(me);
        model.addAttribute("messages", messages);
        model.addAttribute("my_id", me.getId());

        return "frontend/message/my_message_rooms";
    }

    @GetMapping("/job_room/{jobId}/{contractor}")
    public String jobRoom(Model model, 
                          @PathVariable("jobId") long jobId,
                          @PathVariable("contractor") long contractorId) {

        User me = getCurrentUser();
        if (me == null) {
            return "redirect:/login";  // ✅ Redirects if user is not logged in
        }

        Job job = jobService.get(jobId);
        User contractor = userService.get(contractorId);

        if (contractor == null) {
            return "redirect:/message";  // ✅ Prevents NullPointerException
        }

        String contactUrl = "/profile/" + contractor.getId();

        Bid bid = null;
        List<Message> messages = null;

        if (job != null) {
            bid = job.getAuthor().getId().equals(me.getId())
                    ? bidService.getUsersBidByJob(contractor, job)
                    : bidService.getUsersBidByJob(me, job);

            if (!job.getAuthor().getId().equals(me.getId())) {
                contactUrl = "/profile/client/" + contractor.getId();
            }

            messages = messageService.findByJobAndContractor(job, contractor);
        } else {
            messages = messageService.findByMyConversers(me, contractor);
        }

        model.addAttribute("job", job);
        model.addAttribute("contact", contractor);
        model.addAttribute("contact_url", contactUrl);
        model.addAttribute("bid", bid);
        model.addAttribute("messages", messages);
        model.addAttribute("message_room_page_size", messageRoomPageSize);
        model.addAttribute("me", me);

        return "frontend/message/job_room";
    }

    @PostMapping("/job_room/{jobId}/{contractor}")
    public String sendMessageToJobRoom(
            HttpServletRequest request,
            @PathVariable("jobId") long jobId,
            @PathVariable("contractor") long contractorId) throws Exception {

        User me = getCurrentUser();
        if (me == null) {
            return "redirect:/login";  // ✅ Prevents NullPointerException
        }

        Job job = jobService.get(jobId);
        User contractor = userService.get(contractorId);

        if (job == null || contractor == null) {
            return "redirect:/message";  // ✅ Prevents NullPointerException
        }

        // Check if the user has rights to add a message
        if (!job.getAuthor().getId().equals(me.getId()) && !job.getAuthor().getId().equals(contractor.getId())) {
            throw new Exception("Current user does not have permission to write a message in this job");
        }

        String messageText = request.getParameter("message");

        if (messageText == null || messageText.trim().isEmpty()) {
            return "redirect:/message/job_room/" + jobId + "/" + contractorId;  // ✅ Prevents empty messages
        }

        Message message = new Message();
        message.setJob(job);
        message.setReceiver(contractor);
        message.setSender(me);
        message.setText(messageText);
        message.setCreated(new Date());

        Message result = messageService.save(message);
        if (result == null) {
            throw new Exception("Can't save new message");
        }

        return "redirect:" + request.getHeader("Referer");
    }
}
