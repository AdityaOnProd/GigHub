package com.app.gighub.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.Date;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = true)
    private Job job;

    @Lob // ✅ Ensures proper handling of large text fields
    @Column(nullable = false)
    @Size(min = 2, message = "Message must be at least 2 characters")
    private String text;

    @Temporal(TemporalType.TIMESTAMP) // ✅ Proper annotation for Date
    @Column(nullable = false, updatable = false)
    private Date created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + (sender != null ? sender.getId() : "null") +
                ", receiver=" + (receiver != null ? receiver.getId() : "null") +
                ", job=" + (job != null ? job.getId() : "null") +
                ", text='" + text + '\'' +
                ", created=" + created +
                '}';
    }
}
