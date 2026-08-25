package com.app.gighub.models;

import jakarta.persistence.*;
import java.util.Date;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "job")  // ✅ Prevents SQL conflicts
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @Column(nullable = false)
    private Double budget;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, length = 50)
    private String expertizeLevel;

    @Temporal(TemporalType.TIMESTAMP)  // ✅ Use Date instead of String for timestamps
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date created;

    @ManyToOne(fetch = FetchType.LAZY)  // ✅ Changed from EAGER to LAZY
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Getters & Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpertizeLevel() {
        return expertizeLevel;
    }

    public void setExpertizeLevel(String expertizeLevel) {
        this.expertizeLevel = expertizeLevel;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", budget=" + budget +
                ", type='" + type + '\'' +
                ", expertizeLevel='" + expertizeLevel + '\'' +
                ", created=" + created +
                ", author=" + (author != null ? author.getId() : "null") +
                ", category=" + (category != null ? category.getId() : "null") +
                '}';
    }
}
