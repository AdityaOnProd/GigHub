package com.app.gighub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String biography = "";
    private String linkedin = "";
    private String location = "";

    @OneToOne(cascade = CascadeType.ALL) // ✅ Ensures profile is persisted/deleted with user
    @JoinColumn(name = "user_id", nullable = false, unique = true) // ✅ Explicit foreign key column
    @JsonBackReference // ✅ Prevents infinite recursion in JSON serialization
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBiography() {
        return biography != null ? biography : "";
    }

    public void setBiography(String biography) {
        this.biography = biography != null ? biography : "";
    }

    public String getLinkedin() {
        return linkedin != null ? linkedin : "";
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin != null ? linkedin : "";
    }

    public String getLocation() {
        return location != null ? location : "";
    }

    public void setLocation(String location) {
        this.location = location != null ? location : "";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", biography='" + biography + '\'' +
                ", linkedin='" + linkedin + '\'' +
                ", location='" + location + '\'' +
                ", user_id=" + (user != null ? user.getId() : "null") +
                '}';
    }
}
