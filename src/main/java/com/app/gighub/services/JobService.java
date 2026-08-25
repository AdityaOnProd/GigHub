package com.app.gighub.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.gighub.models.Job;
import com.app.gighub.models.User;
import com.app.gighub.repositories.JobRepository;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public Job get(long id) {
        return jobRepository.findById(id).orElse(null);
    }

    public Job add(Job job) {
        job.setType(job.getType().toLowerCase());
        job.setExpertizeLevel(job.getExpertizeLevel().toLowerCase());

        if (job.getCreated() == null) {  // ✅ Corrected to set Date
            job.setCreated(new Date());
        }

        return jobRepository.save(job);
    }

    public List<Job> list() {
        List<Job> result = jobRepository.findAll();
        result.sort((j1, j2) -> j1.getId() > j2.getId() ? -1 : 0);
        return result;
    }

    public List<Job> list(Map<String, Object> filter) {
        List<Job> result = null;
        User usr = (User) filter.get("user");

        if (usr != null) {
            result = jobRepository.findByAuthor(usr);
        }

        if (result != null) {
            result.sort((j1, j2) -> j1.getId() > j2.getId() ? -1 : 0);
        }

        return result;
    }

    public List<Job> findByAuthor(User user) {
        return jobRepository.findByAuthor(user);
    }

    public List<Job> findHiredJobsByAuthor(User user) {
        return jobRepository.findByAuthorAndHired(user);
    }

    public Page<Job> findAllPaged(Map<String, Object> filter, Integer pageNumber, int pageSize) {
        // ✅ Updated to use PageRequest.of()
        PageRequest request = PageRequest.of(pageNumber - 1, pageSize, Sort.by(Sort.Direction.DESC, "id"));

        User user = (User) filter.get("user");
        if (user != null) {
            return jobRepository.findByAuthor(user, request);
        } else {
            return jobRepository.findAll(request);
        }
    }
}
