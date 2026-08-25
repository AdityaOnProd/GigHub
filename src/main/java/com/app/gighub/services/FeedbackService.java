package com.app.gighub.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.gighub.models.Bid;
import com.app.gighub.models.Feedback;
import com.app.gighub.models.Job;
import com.app.gighub.models.User;
import com.app.gighub.repositories.FeedbackRepository;

@Service
public class FeedbackService {

	@Autowired
	FeedbackRepository feedbackRepository;

	public Feedback save(Feedback feedback) {
		return feedbackRepository.save(feedback);
	}

	public Feedback get(Long id) {
		return feedbackRepository.findById(id).orElse(null);
	}
    
    public Feedback findByBid(Bid bid){
        return feedbackRepository.findByBid(bid);
    }

	public List<Feedback> findByClient(User user) {
		return feedbackRepository.findByClient(user);
	}
	
	public Feedback findByJob(Job job) {
		return feedbackRepository.findByJob(job);
	}
	
	public List<Feedback> findByBids(List<Bid> bids){
		return feedbackRepository.findByBidIn(bids);
	}

}
