package com.app.gighub.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double price;

    @Column(length = 255)
    private String deadline;

    @Column(columnDefinition = "TEXT")  // ✅ Proper way to store long text in MySQL
    private String proposal;

    private int accepted = 0;
    private int closed = 0;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // ✅ Explicit foreign key definition
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)  // ✅ Explicit foreign key for Job
    private Job job;

    private LocalDateTime created;  // ✅ Use LocalDateTime instead of String

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreated() {  // ✅ Use LocalDateTime
        return created;
    }

    public void setCreated(LocalDateTime created) {  // ✅ Use LocalDateTime
        this.created = created;
    }

    public String getProposal() {
        return proposal;
    }

    public void setProposal(String proposal) {
        this.proposal = proposal;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public int getClosed() {
        return closed;
    }

    public void setClosed(int closed) {
        this.closed = closed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
